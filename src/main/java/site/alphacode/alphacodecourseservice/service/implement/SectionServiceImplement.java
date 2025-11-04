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
import site.alphacode.alphacodecourseservice.dto.response.AccountLessonDto;
import site.alphacode.alphacodecourseservice.dto.response.SectionDto;
import site.alphacode.alphacodecourseservice.dto.response.SectionWithAccountLesson;
import site.alphacode.alphacodecourseservice.entity.AccountLesson;
import site.alphacode.alphacodecourseservice.entity.Course;
import site.alphacode.alphacodecourseservice.entity.Lesson;
import site.alphacode.alphacodecourseservice.entity.Section;
import site.alphacode.alphacodecourseservice.exception.ConflictException;
import site.alphacode.alphacodecourseservice.exception.ResourceNotFoundException;
import site.alphacode.alphacodecourseservice.mapper.LessonMapper;
import site.alphacode.alphacodecourseservice.mapper.SectionMapper;
import site.alphacode.alphacodecourseservice.repository.AccountLessonRepository;
import site.alphacode.alphacodecourseservice.repository.CourseRepository;
import site.alphacode.alphacodecourseservice.repository.LessonRepository;
import site.alphacode.alphacodecourseservice.repository.SectionRepository;
import site.alphacode.alphacodecourseservice.service.SectionService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SectionServiceImplement implements SectionService {

    private final SectionRepository sectionRepository;
    private final CourseRepository courseRepository;
    // Inject lesson repository to fetch lessons for sections
    private final LessonRepository lessonRepository;
    private final org.springframework.cache.CacheManager cacheManager;
    private final AccountLessonRepository accountLessonRepository;

    @Override
    @Cacheable(value = "section", key = "#id")
    public SectionDto getById(UUID id) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Section với id " + id + " không tồn tại."));
        SectionDto dto = SectionMapper.toDto(section);
        // Populate lessons list ordered and non-deleted
        var lessons = lessonRepository.findAllNoneDeletedBySectionIdOrderByOrderNumberAsc(section.getId())
                .stream()
                .map(LessonMapper::toDto)
                .toList();
        dto.setLessons(lessons);
        return dto;
    }

    @Override
    @Cacheable(value = "sections_list", key = "{#courseId}")
    public List<SectionDto> getAllByCourseId(UUID courseId) {
        courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course với id " + courseId + " không tồn tại."));

        var sections = sectionRepository.findAllByCourseId(courseId);

        return sections.stream()
                .map(s -> {
                    var dto = SectionMapper.toDto(s);
                    var lessons = lessonRepository.findAllNoneDeletedBySectionIdOrderByOrderNumberAsc(s.getId())
                            .stream()
                            .map(LessonMapper::toDto)
                            .toList();
                    dto.setLessons(lessons);
                    return dto;
                })
                .toList();
    }

    @Override
    public List<SectionWithAccountLesson> getAllSectionWithAccountLesson(UUID courseId, UUID accountId) {
        // Kiểm tra course tồn tại
        courseRepository.findNoneDeleteCourseById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course với id " + courseId + " không tồn tại."));

        // Lấy tất cả sections của course
        List<Section> sections = sectionRepository.findAllByCourseId(courseId);

        // Gán lessons cho từng section, giữ thứ tự
        sections.forEach(section -> {
            List<Lesson> lessons = lessonRepository.findAllBySectionIdOrderByOrderNumberAsc(section.getId());
            if (lessons == null) {
                lessons = new ArrayList<>();
            }
            // Dùng LinkedHashSet để giữ thứ tự
            section.setLessons(lessons);
        });

        // Lấy tất cả AccountLesson của account và course
        List<AccountLesson> accountLessons = accountLessonRepository.findAllByAccountIdAndCourseId(accountId, courseId);

        // Chuyển sang Map để truy xuất nhanh theo lessonId
        Map<UUID, AccountLesson> accountLessonMap = accountLessons.stream()
                .collect(Collectors.toMap(a -> a.getLesson().getId(), a -> a));

        // Map từng section sang SectionWithAccountLesson
        return (List<SectionWithAccountLesson>) sections.stream().map(section -> {
            // Copy ra List để xử lý, tránh sửa trực tiếp Set gốc
            List<Lesson> lessonList = new ArrayList<>(section.getLessons());

            // Map từng lesson sang AccountLessonDto
            List<AccountLessonDto> accountLessonDtos = (List<AccountLessonDto>) lessonList.stream()
                    .map(lesson -> {
                        AccountLesson accLesson = accountLessonMap.get(lesson.getId());
                        return AccountLessonDto.builder()
                                .id(accLesson != null ? accLesson.getId() : null)
                                .accountId(accountId)
                                .lessonId(lesson.getId())
                                .status(accLesson != null ? accLesson.getStatus() : 0)
                                .completedAt(accLesson != null ? accLesson.getCompletedAt() : null)
                                .lesson(LessonMapper.toDto(lesson))
                                .build();
                    })
                    .toList();


            // Build SectionWithAccountLesson
            return SectionWithAccountLesson.builder()
                    .id(section.getId())
                    .title(section.getTitle())
                    .orderNumber(section.getOrderNumber())
                    .courseId(section.getCourseId())
                    .accountLessons(accountLessonDtos)
                    .status(section.getStatus())
                    .build();
        }).toList();
    }

    @Override
    public List<SectionWithAccountLesson> getAllSectionWithAccountLessonBySlug(String slug, UUID accountId) {
        // Kiểm tra course tồn tại
        var course = courseRepository.findCourseBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Course với slug " + slug + " không tồn tại."));

        // Lấy tất cả sections của course
        List<Section> sections = sectionRepository.findAllByCourseId(course.getId());

        // Gán lessons cho từng section, giữ thứ tự
        sections.forEach(section -> {
            List<Lesson> lessons = lessonRepository.findAllBySectionIdOrderByOrderNumberAsc(section.getId());
            if (lessons == null) {
                lessons = new ArrayList<>();
            }
            // Dùng LinkedHashSet để giữ thứ tự
            section.setLessons(lessons);
        });

        // Lấy tất cả AccountLesson của account và course
        List<AccountLesson> accountLessons = accountLessonRepository.findAllByAccountIdAndCourseId(accountId, course.getId());

        // Chuyển sang Map để truy xuất nhanh theo lessonId
        Map<UUID, AccountLesson> accountLessonMap = accountLessons.stream()
                .collect(Collectors.toMap(a -> a.getLesson().getId(), a -> a));

        // Map từng section sang SectionWithAccountLesson
        return (List<SectionWithAccountLesson>) sections.stream().map(section -> {
            // Copy ra List để xử lý, tránh sửa trực tiếp Set gốc
            List<Lesson> lessonList = new ArrayList<>(section.getLessons());

            // Map từng lesson sang AccountLessonDto
            List<AccountLessonDto> accountLessonDtos = (List<AccountLessonDto>) lessonList.stream()
                    .map(lesson -> {
                        AccountLesson accLesson = accountLessonMap.get(lesson.getId());
                        return AccountLessonDto.builder()
                                .id(accLesson != null ? accLesson.getId() : null)
                                .accountId(accountId)
                                .lessonId(lesson.getId())
                                .status(accLesson != null ? accLesson.getStatus() : 0)
                                .completedAt(accLesson != null ? accLesson.getCompletedAt() : null)
                                .lesson(LessonMapper.toDto(lesson))
                                .build();
                    })
                    .toList();


            // Build SectionWithAccountLesson
            return SectionWithAccountLesson.builder()
                    .id(section.getId())
                    .title(section.getTitle())
                    .orderNumber(section.getOrderNumber())
                    .courseId(section.getCourseId())
                    .accountLessons(accountLessonDtos)
                    .status(section.getStatus())
                    .build();
        }).toList();
    }



    @Override
    @Transactional
    @CachePut(value = "section", key = "{#result.id}")
    @Caching(evict = {
            @CacheEvict(value = "sections_list", allEntries = true)
    })
    public SectionDto create(CreateSection createSection) {
        Course course = courseRepository.findById(createSection.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course với id " + createSection.getCourseId() + " không tồn tại."));

        sectionRepository.findByTitleAndCourseId(createSection.getTitle(), createSection.getCourseId())
                .ifPresent(s -> {
                    throw new ConflictException("Section title đã tồn tại trong course này.");
                });

        // Lấy orderNumber lớn nhất trong course
        Integer maxOrder = sectionRepository.findMaxOrderNumberByCourseId(createSection.getCourseId());
        int newOrderNumber = (maxOrder == null) ? 1 : maxOrder + 1; // Nếu chưa có section nào -> 1

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

        SectionDto dto = SectionMapper.toDto(saved);
        // Newly created section has no lessons yet
        dto.setLessons(Collections.emptyList());
        return dto;
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
                    .ifPresent(s -> {
                        throw new ConflictException("Section title đã tồn tại trong course này.");
                    });
        }

        existing.setTitle(updateSection.getTitle());
        existing.setOrderNumber(updateSection.getOrderNumber());
        existing.setLastUpdated(LocalDateTime.now());

        Section saved = sectionRepository.save(existing);

        SectionDto dto = SectionMapper.toDto(saved);
        // Populate lessons for updated section
        var lessons = lessonRepository.findAllNoneDeletedBySectionIdOrderByOrderNumberAsc(saved.getId())
                .stream()
                .map(LessonMapper::toDto)
                .toList();
        dto.setLessons(lessons);
        return dto;
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

        Course course = existing.getCourse();
        UUID courseId = existing.getCourseId();

        // Remember order to shift subsequent sections
        int deletedOrder = existing.getOrderNumber();

        // Delete all lessons in this section first
        var lessonsInSection = lessonRepository.findAllNoneDeletedBySectionIdOrderByOrderNumberAsc(existing.getId());
        if (!lessonsInSection.isEmpty()) {
            lessonRepository.deleteAll(lessonsInSection);
        }

        // Delete the section
        sectionRepository.delete(existing);

        // Shift down orderNumber for sections in the same course that were after the
        // deleted section
        var sectionsInCourse = sectionRepository.findAllByCourseId(courseId);
        var sectionsToUpdate = sectionsInCourse.stream()
                .filter(s -> s.getOrderNumber() > deletedOrder)
                .peek(s -> {
                    s.setOrderNumber(s.getOrderNumber() - 1);
                    s.setLastUpdated(LocalDateTime.now());
                })
                .toList();
        if (!sectionsToUpdate.isEmpty()) {
            sectionRepository.saveAll(sectionsToUpdate);
        }

        // Recalculate course aggregates (total lessons & duration)
        int totalDuration = lessonRepository.sumDurationByCourseId(courseId).orElse(0);
        int totalLessons = lessonRepository.countByCourseId(courseId);
        course.setTotalDuration(totalDuration);
        course.setTotalLessons(totalLessons);
        course.setLastUpdated(LocalDateTime.now());
        courseRepository.save(course);

        // Evict course cache
        org.springframework.cache.Cache courseCache = cacheManager.getCache("course");
        if (courseCache != null) {
            courseCache.evict(course.getId());
            courseCache.evict(course.getSlug());
        }
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "section", allEntries = true),
            @CacheEvict(value = "sections_list", allEntries = true)
    })
    public void reorder(UUID courseId, ReorderSectionsRequest request) {
        var items = request.getSections();
        if (items == null || items.isEmpty())
            return;

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
