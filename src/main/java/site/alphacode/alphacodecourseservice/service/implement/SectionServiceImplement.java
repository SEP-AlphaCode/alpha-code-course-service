package site.alphacode.alphacodecourseservice.service.implement;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateSection;
import site.alphacode.alphacodecourseservice.dto.request.update.UpdateSection;
import site.alphacode.alphacodecourseservice.dto.response.PagedResult;
import site.alphacode.alphacodecourseservice.dto.response.SectionDto;
import site.alphacode.alphacodecourseservice.entity.Course;
import site.alphacode.alphacodecourseservice.entity.Section;
import site.alphacode.alphacodecourseservice.exception.ConflictException;
import site.alphacode.alphacodecourseservice.exception.ResourceNotFoundException;
import site.alphacode.alphacodecourseservice.mapper.SectionMapper;
import site.alphacode.alphacodecourseservice.repository.CourseRepository;
import site.alphacode.alphacodecourseservice.repository.SectionRepository;
import site.alphacode.alphacodecourseservice.service.SectionService;

import java.time.LocalDateTime;
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
    @Cacheable(value = "sections_list", key = "{#courseId, #page, #size}")
    public PagedResult<SectionDto> getAllByCourseId(UUID courseId, int page, int size) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course với id " + courseId + " không tồn tại."));

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("orderNumber").ascending());
        var pagedSections = sectionRepository.findAllByCourseId(courseId, pageable);
        return new PagedResult<>(pagedSections.map(SectionMapper::toDto));
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
}
