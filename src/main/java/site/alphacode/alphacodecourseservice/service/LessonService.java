package site.alphacode.alphacodecourseservice.service;

import org.springframework.web.multipart.MultipartFile;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateLesson;
import site.alphacode.alphacodecourseservice.dto.request.patch.PatchLesson;
import site.alphacode.alphacodecourseservice.dto.request.update.UpdateLesson;
import site.alphacode.alphacodecourseservice.dto.response.LessonDto;
import site.alphacode.alphacodecourseservice.dto.response.LessonWithSolution;
import site.alphacode.alphacodecourseservice.dto.response.PagedResult;

import java.util.UUID;

public interface LessonService {

    // Lấy Lesson theo id
    LessonDto getLessonById(UUID id);
    LessonWithSolution getLessonWithSolutionById(UUID id);

    // Thao tác CRUD
    LessonWithSolution create(CreateLesson createLesson, MultipartFile videoFile);
    LessonWithSolution update(UUID lessonId, UpdateLesson updateLesson, MultipartFile videoFile);
    LessonWithSolution patch(UUID lessonId, PatchLesson patchLesson, MultipartFile videoFile);
    void delete(UUID lessonId);

    // Lấy Lesson theo Section
    PagedResult<LessonDto> getActiveLessonsBySectionId(UUID sectionId, int page, int size);
    PagedResult<LessonWithSolution> getAllLessonsWithSolutionBySectionId(UUID sectionId, int page, int size);

    // Lấy Lesson theo Course (qua join Section → Course)
    PagedResult<LessonDto> getActiveLessonsByCourseId(UUID courseId, int page, int size);
    PagedResult<LessonWithSolution> getAllLessonsWithSolutionByCourseId(UUID courseId, int page, int size);
}
