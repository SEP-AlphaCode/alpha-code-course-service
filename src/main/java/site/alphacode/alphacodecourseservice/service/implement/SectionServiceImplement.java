package site.alphacode.alphacodecourseservice.service.implement;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import site.alphacode.alphacodecourseservice.dto.request.ReorderSectionsRequest;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateSection;
import site.alphacode.alphacodecourseservice.dto.request.update.UpdateSection;
import site.alphacode.alphacodecourseservice.dto.response.SectionDto;
import site.alphacode.alphacodecourseservice.entity.Course;
import site.alphacode.alphacodecourseservice.entity.Section;
import site.alphacode.alphacodecourseservice.exception.ConflictException;
import site.alphacode.alphacodecourseservice.exception.ResourceNotFoundException;
import site.alphacode.alphacodecourseservice.mapper.SectionMapper;
import site.alphacode.alphacodecourseservice.repository.CourseRepository;
import site.alphacode.alphacodecourseservice.repository.SectionRepository;
import site.alphacode.alphacodecourseservice.repository.LessonRepository;
import site.alphacode.alphacodecourseservice.service.SectionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SectionServiceImplement implements SectionService {

    private final SectionRepository sectionRepository;
    private final CourseRepository courseRepository;

    @Override
    @Cacheable(value = "section", key = "#id")
    public SectionDto getById(UUID id) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Section với id " + id + " không tồn tại."));
        return SectionMapper.toDto(section);
    }

    @Override
    @Cacheable(value = "sections_list", key = "{#courseId}")
    public List<SectionDto> getAllByCourseId(UUID courseId) {
        courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course với id " + courseId + " không tồn tại."));

        var sections = sectionRepository.findAllByCourseId(courseId);

        return sections.stream()
                .map(SectionMapper::toDto)
                .toList();
    }



    @Override
    @Transactional
    @CachePut(value = "section", key = "{#result.id}")
    @Caching(evict = {
            @CacheEvict(value = "sections_list", allEntries = true)
    })
    public SectionDto create(CreateSection createSection) {
        Course course = courseRepository.findById(createSection.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course với id " + createSection.getCourseId() + " không tồn tại."));

        sectionRepository.findByTitleAndCourseId(createSection.getTitle(), createSection.getCourseId())
                .ifPresent(s -> { throw new ConflictException("Section title đã tồn tại trong course này."); });

        // Lấy orderNumber lớn nhất trong course
        Integer maxOrder = sectionRepository.findMaxOrderNumberByCourseId(createSection.getCourseId());
        int newOrderNumber = (maxOrder == null) ? 1 : maxOrder + 1;  // Nếu chưa có section nào -> 1

        Section section = Section.builder()
                .title(createSection.getTitle())
                .orderNumber(newOrderNumber)
                .courseId(createSection.getCourseId())
                .createdDate(LocalDateTime.now())
                .status(1) // Active by default
                .lastUpdated(null)
                .build();

        Section saved = sectionRepository.save(section);

        // Update course lastUpdated
        course.setLastUpdated(LocalDateTime.now());
        courseRepository.save(course);

        return SectionMapper.toDto(saved);
    }

    @Override
    @Transactional
    @CachePut(value = "section", key = "#sectionId")
    @Caching(evict = {
            @CacheEvict(value = "sections_list", allEntries = true)
    })
    public SectionDto update(UUID sectionId, UpdateSection updateSection) {
        Section existing = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section với id " + sectionId + " không tồn tại."));

        if (!existing.getTitle().equals(updateSection.getTitle())) {
            sectionRepository.findByTitleAndCourseId(updateSection.getTitle(), existing.getCourseId())
                    .ifPresent(s -> { throw new ConflictException("Section title đã tồn tại trong course này."); });
        }

        existing.setTitle(updateSection.getTitle());
        existing.setOrderNumber(updateSection.getOrderNumber());
        existing.setLastUpdated(LocalDateTime.now());

        Section saved = sectionRepository.save(existing);

        return SectionMapper.toDto(saved);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "section", key = "#sectionId"),
            @CacheEvict(value = "sections_list", allEntries = true)
    })
    public void delete(UUID sectionId) {
        Section existing = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section với id " + sectionId + " không tồn tại."));
        sectionRepository.delete(existing);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "section", allEntries = true),
            @CacheEvict(value = "sections_list", allEntries = true)
    })
    public void reorder(ReorderSectionsRequest request) {
        var items = request.getSections();
        if (items == null || items.isEmpty()) return;

        // Build maps and validate duplicates
        var idToOrder = new java.util.HashMap<UUID, Integer>();
        for (var item : items) {
            if (idToOrder.put(item.getId(), item.getOrderNumber()) != null) {
                throw new ConflictException("Trùng lặp section id trong danh sách sắp xếp.");
            }
        }

        var ids = idToOrder.keySet();
        var sections = sectionRepository.findAllById(ids);
        if (sections.size() != ids.size()) {
            throw new ResourceNotFoundException("Một hoặc nhiều section không tồn tại.");
        }

        // Determine course and ensure all sections belong to same course
        UUID courseId = request.getCourseId();
        if (courseId == null) {
            courseId = sections.iterator().next().getCourseId();
        }
        final UUID expectedCourseId = courseId;
        boolean allSameCourse = sections.stream().allMatch(s -> s.getCourseId().equals(expectedCourseId));
        if (!allSameCourse) {
            throw new ConflictException("Tất cả section phải thuộc cùng một khóa học.");
        }

        // Apply new order
        final var now = java.time.LocalDateTime.now();
        sections.forEach(s -> {
            Integer newOrder = idToOrder.get(s.getId());
            if (newOrder != null) {
                s.setOrderNumber(newOrder);
                s.setLastUpdated(now);
            }
        });

        sectionRepository.saveAll(sections);

        // Update course lastUpdated
        courseRepository.findById(expectedCourseId).ifPresent(c -> {
            c.setLastUpdated(now);
            courseRepository.save(c);
        });
    }


}
