package site.alphacode.alphacodecourseservice.service.implement;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateLesson;
import site.alphacode.alphacodecourseservice.dto.request.patch.PatchLesson;
import site.alphacode.alphacodecourseservice.dto.request.update.UpdateLesson;
import site.alphacode.alphacodecourseservice.dto.response.LessonDto;
import site.alphacode.alphacodecourseservice.dto.response.LessonWithSolution;
import site.alphacode.alphacodecourseservice.entity.Course;
import site.alphacode.alphacodecourseservice.entity.Lesson;
import site.alphacode.alphacodecourseservice.exception.ConflictException;
import site.alphacode.alphacodecourseservice.exception.ResourceNotFoundException;
import site.alphacode.alphacodecourseservice.mapper.LessonMapper;
import site.alphacode.alphacodecourseservice.repository.CourseRepository;
import site.alphacode.alphacodecourseservice.repository.LessonRepository;
import site.alphacode.alphacodecourseservice.service.LessonService;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LessonServiceImplement implements LessonService {
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;

    @Override
    @Cacheable(value = "lesson", key = "#id")
    public LessonDto getLessonById(UUID id) {
        var lesson = lessonRepository.findById(id);
            if (lesson.isEmpty()) {
                throw new ResourceNotFoundException("Khóa học với id " + id + " không tồn tại.");
            }

        return LessonMapper.toDto(lesson.get());
    }

    @Override
    @Cacheable(value = "lesson_with_solution", key = "#id")
    public LessonWithSolution getLessonWithSolutionById(UUID id) {
        var lesson = lessonRepository.findById(id);
            if (lesson.isEmpty()) {
                throw new ResourceNotFoundException("Khóa học với id " + id + " không tồn tại.");
            }

        return LessonMapper.toLessonWithSolution(lesson.get());
    }

    @Override
    @Transactional
    @CachePut(value = "lesson", key = "{#result.id}")
    @CacheEvict(value = "lesson_with_solution", allEntries = true)
    public LessonWithSolution create (CreateLesson createLesson){
        var lesson = lessonRepository.findByTitle(createLesson.getTitle());
        if (!lesson.isEmpty()) {
            throw new ConflictException("Tiêu đề bài học đã tồn tại.");
        }
        Course course = courseRepository.findNoneDeleteCourseById(createLesson.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Khóa học với id " + createLesson.getCourseId() + " không tồn tại."
                ));

        int maxOrder = lessonRepository.findMaxOrderNumberByCourseId(createLesson.getCourseId())
                .orElse(0);

        Lesson newLesson = new Lesson();
        newLesson.setContent(createLesson.getContent());
        newLesson.setContentType(createLesson.getContentType());
        newLesson.setCourseId(createLesson.getCourseId());
        newLesson.setDuration(createLesson.getDuration());
        newLesson.setOrderNumber(maxOrder + 1);
        newLesson.setRequireRobot(createLesson.getRequireRobot());
        newLesson.setSolution(createLesson.getSolution());
        newLesson.setStatus(1);
        newLesson.setTitle(createLesson.getTitle());
        newLesson.setCreatedDate(LocalDateTime.now());
        newLesson.setLastUpdated(null);

        Lesson saved = lessonRepository.save(newLesson);

        course.setTotalLessons(course.getTotalLessons() + 1);
        course.setLastUpdated(LocalDateTime.now());
        course.setTotalDuration(course.getTotalDuration() + saved.getDuration());
        courseRepository.save(course);

        // 6. Trả về DTO hoặc wrapper
        return LessonMapper.toLessonWithSolution(saved);
    }

    @Override
    @Transactional
    @CachePut(value = "lesson", key = "{#lessonId}")
    @CacheEvict(value = "lesson_with_solution", allEntries = true)
    public LessonWithSolution update(UUID lessonId, UpdateLesson updateLesson) {
        Lesson existing = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bài học với id " + lessonId + " không tồn tại."
                ));

        // check trùng title (nếu có update title)
        if (!existing.getTitle().equals(updateLesson.getTitle())) {
            lessonRepository.findByTitle(updateLesson.getTitle())
                    .ifPresent(l -> {
                        throw new ConflictException("Tiêu đề bài học đã tồn tại.");
                    });
        }

        Course course = courseRepository.findNoneDeleteCourseById(updateLesson.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Khóa học với id " + updateLesson.getCourseId() + " không tồn tại."
                ));

        // Cập nhật duration cho course (nếu thay đổi)
        if (!Objects.equals(existing.getDuration(), updateLesson.getDuration())) {
            int oldDuration = existing.getDuration();
            int newDuration = updateLesson.getDuration();
            course.setTotalDuration(course.getTotalDuration() - oldDuration + newDuration);
        }

        existing.setTitle(updateLesson.getTitle());
        existing.setContent(updateLesson.getContent());
        existing.setContentType(updateLesson.getContentType());
        existing.setDuration(updateLesson.getDuration());
        existing.setRequireRobot(updateLesson.getRequireRobot());
        existing.setSolution(updateLesson.getSolution());
        existing.setStatus(updateLesson.getStatus());
        existing.setLastUpdated(LocalDateTime.now());

        Lesson saved = lessonRepository.save(existing);

        // update course lastUpdated
        course.setLastUpdated(LocalDateTime.now());
        courseRepository.save(course);

        return LessonMapper.toLessonWithSolution(saved);
    }

    @Override
    @Transactional
    @CachePut(value = "lesson", key = "{#lessonId}")
    @CacheEvict(value = "lesson_with_solution", allEntries = true)
    public LessonWithSolution patch(UUID lessonId, PatchLesson patchLesson) {
        Lesson existing = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bài học với id " + lessonId + " không tồn tại."
                ));

        Course course = courseRepository.findNoneDeleteCourseById(existing.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Khóa học với id " + existing.getCourseId() + " không tồn tại."
                ));

        boolean durationChanged = false;
        int oldDuration = existing.getDuration();

        if (patchLesson.getTitle() != null && !existing.getTitle().equals(patchLesson.getTitle())) {
            lessonRepository.findByTitle(patchLesson.getTitle())
                    .ifPresent(l -> {
                        throw new ConflictException("Tiêu đề bài học đã tồn tại.");
                    });
            existing.setTitle(patchLesson.getTitle());
        }

        if (patchLesson.getContent() != null) {
            existing.setContent(patchLesson.getContent());
        }
        if (patchLesson.getContentType() != null) {
            existing.setContentType(patchLesson.getContentType());
        }
        if (patchLesson.getDuration() != null) {
            existing.setDuration(patchLesson.getDuration());
            durationChanged = true;
        }
        if (patchLesson.getOrderNumber() != null) {
            existing.setOrderNumber(patchLesson.getOrderNumber());
        }
        if (patchLesson.getRequireRobot() != null) {
            existing.setRequireRobot(patchLesson.getRequireRobot());
        }
        if (patchLesson.getSolution() != null) {
            existing.setSolution(patchLesson.getSolution());
        }
        if (patchLesson.getStatus() != null) {
            existing.setStatus(patchLesson.getStatus());
        }

        existing.setLastUpdated(LocalDateTime.now());

        Lesson saved = lessonRepository.save(existing);

        // cập nhật duration course nếu có thay đổi
        if (durationChanged) {
            course.setTotalDuration(course.getTotalDuration() - oldDuration + saved.getDuration());
        }
        course.setLastUpdated(LocalDateTime.now());
        courseRepository.save(course);

        return LessonMapper.toLessonWithSolution(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"lesson", "lesson_with_solution"}, key = "#lessonId", allEntries = true)
    public void delete(UUID lessonId) {
        Lesson existing = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bài học với id " + lessonId + " không tồn tại."
                ));

        Course course = courseRepository.findNoneDeleteCourseById(existing.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Khóa học với id " + existing.getCourseId() + " không tồn tại."
                ));

        lessonRepository.delete(existing);

        // Cập nhật lại totalLessons và totalDuration của course
        course.setTotalLessons(course.getTotalLessons() - 1);
        course.setTotalDuration(course.getTotalDuration() - existing.getDuration());
        course.setLastUpdated(LocalDateTime.now());
        courseRepository.save(course);
    }

}
