package site.alphacode.alphacodecourseservice.service.implement;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import site.alphacode.alphacodecourseservice.dto.request.ReorderLessonsRequest;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateLesson;
import site.alphacode.alphacodecourseservice.dto.request.patch.PatchLesson;
import site.alphacode.alphacodecourseservice.dto.request.update.UpdateLesson;
import site.alphacode.alphacodecourseservice.dto.response.LessonDto;
import site.alphacode.alphacodecourseservice.dto.response.LessonWithSolution;
import site.alphacode.alphacodecourseservice.dto.response.PagedResult;
import site.alphacode.alphacodecourseservice.entity.Course;
import site.alphacode.alphacodecourseservice.entity.Lesson;
import site.alphacode.alphacodecourseservice.entity.Section;
import site.alphacode.alphacodecourseservice.exception.ConflictException;
import site.alphacode.alphacodecourseservice.exception.ResourceNotFoundException;
import site.alphacode.alphacodecourseservice.mapper.LessonMapper;
import site.alphacode.alphacodecourseservice.repository.CourseRepository;
import site.alphacode.alphacodecourseservice.repository.LessonRepository;
import site.alphacode.alphacodecourseservice.repository.SectionRepository;
import site.alphacode.alphacodecourseservice.service.LessonService;
import site.alphacode.alphacodecourseservice.util.SlugHelper;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LessonServiceImplement implements LessonService {

    private final LessonRepository lessonRepository;
    private final SectionRepository sectionRepository;
    private final CourseRepository courseRepository;
    // S3 uploads are now done on FE via presigned URLs
    private final CacheManager cacheManager;

    // ==================== GET ====================
    @Override
    @Cacheable(value = "lesson", key = "#id")
    public LessonDto getLessonById(UUID id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bài học với id " + id + " không tồn tại."));
        return LessonMapper.toDto(lesson);
    }

    @Override
    @Cacheable(value = "lessons_list", key = "{#sectionId, #page, #size}")
    public PagedResult<LessonDto> getActiveLessonsBySectionId(UUID sectionId, int page, int size) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section với id " + sectionId + " không tồn tại."));

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("orderNumber").ascending());
        var pagedLessons = lessonRepository.findAllActiveLessonsBySectionId(sectionId, pageable);
        return new PagedResult<>(pagedLessons.map(LessonMapper::toDto));
    }

    @Override
    @Cacheable(value = "lessons_with_solution_list", key = "{#sectionId, #page, #size}")
    public PagedResult<LessonWithSolution> getAllLessonsWithSolutionBySectionId(UUID sectionId, int page, int size) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section với id " + sectionId + " không tồn tại."));

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("orderNumber").ascending());
        var pagedLessons = lessonRepository.findAllLessonWithSolutionBySectionId(sectionId, pageable);
        return new PagedResult<>(pagedLessons.map(LessonMapper::toLessonWithSolution));
    }

    @Override
    @Cacheable(value = "lesson_with_solution", key = "#id")
    public LessonWithSolution getLessonWithSolutionById(UUID id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bài học với id " + id + " không tồn tại."));
        return LessonMapper.toLessonWithSolution(lesson);
    }

    @Override
    @Cacheable(value = "lesson_by_slug", key = "#slug")
    public LessonDto getLessonBySlug(String slug) {
        Lesson lesson = lessonRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Bài học với slug " + slug + " không tồn tại."));
        return LessonMapper.toDto(lesson);
    }

    @Override
    @Cacheable(value = "lesson_with_solution_by_slug", key = "#slug")
    public LessonWithSolution getLessonWithSolutionBySlug(String slug) {
        Lesson lesson = lessonRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Bài học với slug " + slug + " không tồn tại."));
        return LessonMapper.toLessonWithSolution(lesson);
    }

    // ==================== CREATE ====================
    @Override
    @Transactional
    @CachePut(value = "lesson", key = "#result.id")
    @Caching(evict = {
            @CacheEvict(value = "lessons_list", allEntries = true),
            @CacheEvict(value = "lessons_with_solution_list", allEntries = true),
            @CacheEvict(value = "lesson_with_solution", allEntries = true),
            @CacheEvict(value = "lesson_by_slug", allEntries = true),
            @CacheEvict(value = "lesson_with_solution_by_slug", allEntries = true)
    })
    public LessonWithSolution create(CreateLesson createLesson) {
        lessonRepository.findByTitleAndSectionId(createLesson.getTitle(), createLesson.getSectionId())
                .ifPresent(l -> { throw new ConflictException("Tiêu đề bài học đã tồn tại."); });

        Section section = sectionRepository.findById(createLesson.getSectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Section với id " + createLesson.getSectionId() + " không tồn tại."));

        String slug = SlugHelper.toSlug(createLesson.getTitle());
        lessonRepository.findBySlug(slug).ifPresent(l -> { throw new ConflictException("Bài học với tên này đã tồn tại."); });

        Course course = section.getCourse();

        int maxOrder = lessonRepository.findMaxOrderNumberBySectionId(createLesson.getSectionId()).orElse(0);

        String videoUrl = createLesson.getVideoUrl();

        Lesson lesson = Lesson.builder()
                .title(createLesson.getTitle())
                .content(createLesson.getContent())
                .videoUrl(videoUrl)
                .duration(createLesson.getDuration())
                .requireRobot(createLesson.getRequireRobot())
                .solution(createLesson.getSolution())
                .orderNumber(maxOrder + 1)
                .status(1)
                .type(createLesson.getType())
                .sectionId(createLesson.getSectionId())
                .createdDate(LocalDateTime.now())
                .lastUpdated(null)
                .slug(slug)
                .build();

        Lesson saved = lessonRepository.save(lesson);

        // Update course
        course.setTotalLessons(course.getTotalLessons() + 1);
        course.setTotalDuration(course.getTotalDuration() + saved.getDuration());
        course.setLastUpdated(LocalDateTime.now());
        courseRepository.save(course);

        // Evict section cache for the affected section and sections_list for the course
        org.springframework.cache.Cache sectionCache = cacheManager.getCache("section");
        if (sectionCache != null) {
            sectionCache.evict(createLesson.getSectionId());
        }
        org.springframework.cache.Cache sectionsListCache = cacheManager.getCache("sections_list");
        if (sectionsListCache != null) {
            sectionsListCache.evict(course.getId());
        }

        // Evict course cache manually
        Cache courseCache = cacheManager.getCache("course");
        if (courseCache != null) {
            courseCache.evict(course.getId());
            courseCache.evict(course.getSlug());
        }

        return LessonMapper.toLessonWithSolution(saved);
    }

    // ==================== UPDATE ====================
    @Override
    @Transactional
    @CachePut(value = "lesson", key = "#lessonId")
    @Caching(evict = {
            @CacheEvict(value = "lessons_list", allEntries = true),
            @CacheEvict(value = "lessons_with_solution_list", allEntries = true),
            @CacheEvict(value = "lesson_with_solution", allEntries = true),
            @CacheEvict(value = "lesson_by_slug", allEntries = true),
            @CacheEvict(value = "lesson_with_solution_by_slug", allEntries = true)
    })
    public LessonWithSolution update(UUID lessonId, UpdateLesson updateLesson) {
        Lesson existing = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Bài học với id " + lessonId + " không tồn tại."));

        String slug = SlugHelper.toSlug(updateLesson.getTitle());

        if (!existing.getTitle().equals(updateLesson.getTitle())) {
            lessonRepository.findByTitleAndSectionId(updateLesson.getTitle(), updateLesson.getSectionId())
                    .ifPresent(l -> { throw new ConflictException("Tiêu đề bài học đã tồn tại."); });


            lessonRepository.findBySlug(slug).ifPresent(l -> { throw new ConflictException("Bài học với tên này đã tồn tại."); });
        }

        Section section = sectionRepository.findById(updateLesson.getSectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Section với id " + updateLesson.getSectionId() + " không tồn tại."));
        Course course = section.getCourse();

        String videoUrl = updateLesson.getVideoUrl() != null ? updateLesson.getVideoUrl() : existing.getVideoUrl();

        if (!Objects.equals(existing.getDuration(), updateLesson.getDuration())) {
            course.setTotalDuration(course.getTotalDuration() - existing.getDuration() + updateLesson.getDuration());
        }

        existing.setTitle(updateLesson.getTitle());
        existing.setContent(updateLesson.getContent());
        existing.setVideoUrl(videoUrl);
        existing.setDuration(updateLesson.getDuration());
        existing.setRequireRobot(updateLesson.getRequireRobot());
        existing.setSolution(updateLesson.getSolution());
        existing.setStatus(updateLesson.getStatus());
        existing.setType(updateLesson.getType());
        existing.setOrderNumber(updateLesson.getOrderNumber());
        existing.setLastUpdated(LocalDateTime.now());
        existing.setSectionId(updateLesson.getSectionId());
        existing.setSlug(slug);

        Lesson saved = lessonRepository.save(existing);

        course.setLastUpdated(LocalDateTime.now());
        courseRepository.save(course);

        // Evict section cache for the affected section and sections_list for the course
        org.springframework.cache.Cache sectionCache = cacheManager.getCache("section");
        if (sectionCache != null) {
            sectionCache.evict(updateLesson.getSectionId());
        }
        org.springframework.cache.Cache sectionsListCache = cacheManager.getCache("sections_list");
        if (sectionsListCache != null) {
            sectionsListCache.evict(course.getId());
        }

        // Evict course cache manually
        Cache courseCache = cacheManager.getCache("course");
        if (courseCache != null) {
            courseCache.evict(course.getId());
            courseCache.evict(course.getSlug());
        }

        return LessonMapper.toLessonWithSolution(saved);
    }

    // ==================== PATCH ====================
    @Override
    @Transactional
    @CachePut(value = "lesson", key = "#lessonId")
    @Caching(evict = {
            @CacheEvict(value = "lessons_list", allEntries = true),
            @CacheEvict(value = "lessons_with_solution_list", allEntries = true),
            @CacheEvict(value = "lesson_with_solution", allEntries = true),
            @CacheEvict(value = "lesson_by_slug", allEntries = true),
            @CacheEvict(value = "lesson_with_solution_by_slug", allEntries = true)
    })
    public LessonWithSolution patch(UUID lessonId, PatchLesson patchLesson) {
        Lesson existing = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Bài học với id " + lessonId + " không tồn tại."));

        Section section = sectionRepository.findById(existing.getSectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Section với id " + existing.getSectionId() + " không tồn tại."));
        Course course = section.getCourse();

        int oldDuration = existing.getDuration();

        if (patchLesson.getTitle() != null && !existing.getTitle().equals(patchLesson.getTitle())) {
            lessonRepository.findByTitleAndSectionId(patchLesson.getTitle(), patchLesson.getSectionId())
                    .ifPresent(l -> { throw new ConflictException("Tiêu đề bài học đã tồn tại."); });
            existing.setTitle(patchLesson.getTitle());
            String slug = SlugHelper.toSlug(patchLesson.getTitle());
            lessonRepository.findBySlug(slug).ifPresent(l -> { throw new ConflictException("Bài học với tên này đã tồn tại."); });

            existing.setSlug(slug);
        }

        if (patchLesson.getVideoUrl() != null) {
            existing.setVideoUrl(patchLesson.getVideoUrl());
        }

        if (patchLesson.getContent() != null) existing.setContent(patchLesson.getContent());
        if (patchLesson.getDuration() != null) existing.setDuration(patchLesson.getDuration());
        if (patchLesson.getOrderNumber() != null) existing.setOrderNumber(patchLesson.getOrderNumber());
        if (patchLesson.getRequireRobot() != null) existing.setRequireRobot(patchLesson.getRequireRobot());
        if (patchLesson.getSolution() != null) existing.setSolution(patchLesson.getSolution());
        if (patchLesson.getStatus() != null) existing.setStatus(patchLesson.getStatus());
        if (patchLesson.getType() != null) existing.setType(patchLesson.getType());

        existing.setLastUpdated(LocalDateTime.now());
        Lesson saved = lessonRepository.save(existing);

        // Update course duration if changed
        if (patchLesson.getDuration() != null) {
            course.setTotalDuration(course.getTotalDuration() - oldDuration + saved.getDuration());
        }
        course.setLastUpdated(LocalDateTime.now());
        courseRepository.save(course);

        // Evict section cache for the affected section and sections_list for the course
        org.springframework.cache.Cache sectionCache = cacheManager.getCache("section");
        if (sectionCache != null) {
            sectionCache.evict(patchLesson.getSectionId());
        }
        org.springframework.cache.Cache sectionsListCache = cacheManager.getCache("sections_list");
        if (sectionsListCache != null) {
            sectionsListCache.evict(course.getId());
        }

        Cache courseCache = cacheManager.getCache("course");
        if (courseCache != null) {
            courseCache.evict(course.getId());
            courseCache.evict(course.getSlug());
        }

        return LessonMapper.toLessonWithSolution(saved);
    }

    // ==================== DELETE ====================
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "lesson", key = "#lessonId"),
            @CacheEvict(value = "lessons_list", allEntries = true),
            @CacheEvict(value = "lessons_with_solution_list", allEntries = true),
            @CacheEvict(value = "lesson_with_solution", allEntries = true),
            @CacheEvict(value = "lesson_by_slug", allEntries = true),
            @CacheEvict(value = "lesson_with_solution_by_slug", allEntries = true)
    })
    public void delete(UUID lessonId) {
        Lesson existing = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Bài học với id " + lessonId + " không tồn tại."));

        Section section = sectionRepository.findById(existing.getSectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Section với id " + existing.getSectionId() + " không tồn tại."));
        Course course = section.getCourse();

        // Remember the order and section before deletion so we can shift subsequent lessons
        int deletedOrder = existing.getOrderNumber();
        UUID sectionId = existing.getSectionId();

        lessonRepository.delete(existing);

        // Shift down orderNumber for lessons in the same section that were after the deleted lesson
        var lessonsInSection = lessonRepository.findAllNoneDeletedBySectionIdOrderByOrderNumberAsc(sectionId);
        var toUpdate = lessonsInSection.stream()
                .filter(l -> l.getOrderNumber() > deletedOrder)
                .peek(l -> {
                    l.setOrderNumber(l.getOrderNumber() - 1);
                    l.setLastUpdated(LocalDateTime.now());
                })
                .toList();
        if (!toUpdate.isEmpty()) {
            lessonRepository.saveAll(toUpdate);
        }

        // Recalculate course aggregates after deletion
        int totalDuration = lessonRepository.sumDurationByCourseId(course.getId()).orElse(0);
        int totalLessons = lessonRepository.countByCourseId(course.getId());

        course.setTotalDuration(totalDuration);
        course.setTotalLessons(totalLessons);
        course.setLastUpdated(LocalDateTime.now());
        courseRepository.save(course);

        // Evict section cache for the affected section and sections_list for the course
        org.springframework.cache.Cache sectionCache = cacheManager.getCache("section");
        if (sectionCache != null) {
            sectionCache.evict(sectionId);
        }
        org.springframework.cache.Cache sectionsListCache = cacheManager.getCache("sections_list");
        if (sectionsListCache != null) {
            sectionsListCache.evict(course.getId());
        }

        Cache courseCache = cacheManager.getCache("course");
        if (courseCache != null) {
            courseCache.evict(course.getId());
            courseCache.evict(course.getSlug());
        }
    }

    // ==================== LIST BY COURSE ====================
    @Override
    @Cacheable(value = "lessons_list", key = "{#courseId, #page, #size}")
    public PagedResult<LessonDto> getActiveLessonsByCourseId(UUID courseId, int page, int size) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Khóa học với id " + courseId + " không tồn tại."));

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("orderNumber").ascending());
        var pagedLessons = lessonRepository.findAllActiveLessonsByCourseId(courseId, pageable);
        return new PagedResult<>(pagedLessons.map(LessonMapper::toDto));
    }

    @Override
    @Cacheable(value = "lessons_with_solution_list", key = "{#courseId, #page, #size}")
    public PagedResult<LessonWithSolution> getAllLessonsWithSolutionByCourseId(UUID courseId, int page, int size) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Khóa học với id " + courseId + " không tồn tại."));

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("orderNumber").ascending());
        var pagedLessons = lessonRepository.findAllLessonWithSolutionByCourseId(courseId, pageable);
        return new PagedResult<>(pagedLessons.map(LessonMapper::toLessonWithSolution));
    }

    @Override
    @Cacheable(value = "lessons_with_solution_list", key = "{#page, #size, #search, #courseId, #sectionId, #type, #requireRobot}")
    public PagedResult<LessonWithSolution> getAllLessons(int page, int size, String search, UUID courseId, UUID sectionId, Integer type, Boolean requireRobot) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdDate").descending());

        if (courseId != null) {
            courseRepository.findById(courseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Khóa học với id " + courseId + " không tồn tại."));
        }
        if (sectionId != null) {
            sectionRepository.findById(sectionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Section với id " + sectionId + " không tồn tại."));
        }

        var pageResult = lessonRepository.findAllWithFilters(search, courseId, sectionId, type, requireRobot, pageable);
        return new PagedResult<>(pageResult.map(LessonMapper::toLessonWithSolution));
    }

    // ==================== REORDER ====================
    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "lesson", allEntries = true),
            @CacheEvict(value = "lessons_list", allEntries = true),
            @CacheEvict(value = "lessons_with_solution_list", allEntries = true),
            @CacheEvict(value = "section", allEntries = true),
            @CacheEvict(value = "sections_list", allEntries = true)
    })
    public void reorderLessons(UUID sectionId, ReorderLessonsRequest request) {
        var items = request.getLessons();
        if (items == null || items.isEmpty()) return;

        // Map: lessonId -> (newOrder, newSectionId)
        var idToOrder = new java.util.HashMap<UUID, Integer>();
        var idToTargetSection = new java.util.HashMap<UUID, UUID>();
        for (var item : items) {
            if (idToOrder.put(item.getId(), item.getOrderNumber()) != null) {
                throw new ConflictException("Trùng lặp lesson id trong danh sách sắp xếp.");
            }
            idToTargetSection.put(item.getId(), item.getSectionId());
        }

        var lessonIds = idToOrder.keySet();
        var lessons = lessonRepository.findAllById(lessonIds);
        if (lessons.size() != lessonIds.size()) {
            throw new ResourceNotFoundException("Một hoặc nhiều lesson không tồn tại.");
        }

        // Validate target sections exist and belong to the same course
        var targetSectionIds = new java.util.HashSet<UUID>(idToTargetSection.values());
        var targetSections = sectionRepository.findAllById(targetSectionIds);
        if (targetSections.size() != targetSectionIds.size()) {
            throw new ResourceNotFoundException("Một hoặc nhiều section đích không tồn tại.");
        }
        UUID expectedCourseId = targetSections.iterator().next().getCourseId();
        boolean allTargetSameCourse = targetSections.stream().allMatch(s -> s.getCourseId().equals(expectedCourseId));
        if (!allTargetSameCourse) {
            throw new ConflictException("Tất cả section đích phải thuộc cùng một khóa học.");
        }

        // Validate existing lessons also belong to the same course
        var currentSectionIds = lessons.stream().map(site.alphacode.alphacodecourseservice.entity.Lesson::getSectionId).collect(java.util.stream.Collectors.toSet());
        var currentSections = sectionRepository.findAllById(currentSectionIds);
        boolean allCurrentSameCourse = currentSections.stream().allMatch(s -> s.getCourseId().equals(expectedCourseId));
        if (!allCurrentSameCourse) {
            throw new ConflictException("Không thể di chuyển bài học giữa các khóa học khác nhau.");
        }

        // Update lessons
        final var now = java.time.LocalDateTime.now();
        lessons.forEach(l -> {
            var newOrder = idToOrder.get(l.getId());
            var newSectionId = idToTargetSection.get(l.getId());
            if (newOrder != null) l.setOrderNumber(newOrder);
            if (newSectionId != null) l.setSectionId(newSectionId);
            l.setLastUpdated(now);
        });

        lessonRepository.saveAll(lessons);

        // Update course lastUpdated
        courseRepository.findById(expectedCourseId).ifPresent(c -> {
            c.setLastUpdated(now);
            courseRepository.save(c);
        });
    }
}
