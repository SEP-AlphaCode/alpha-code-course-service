package site.alphacode.alphacodecourseservice.service;

import site.alphacode.alphacodecourseservice.dto.request.ReorderLessonsRequest;
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

    // Lấy Lesson theo slug
    LessonDto getLessonBySlug(String slug);
    LessonWithSolution getLessonWithSolutionBySlug(String slug);

    // Thao tác CRUD (JSON only; FE uploads video separately and passes videoUrl)
    LessonWithSolution create(CreateLesson createLesson);
    LessonWithSolution update(UUID lessonId, UpdateLesson updateLesson);
    LessonWithSolution patch(UUID lessonId, PatchLesson patchLesson);
    void delete(UUID lessonId);

    // Lấy Lesson theo Section
    PagedResult<LessonDto> getActiveLessonsBySectionId(UUID sectionId, int page, int size);
    PagedResult<LessonWithSolution> getAllLessonsWithSolutionBySectionId(UUID sectionId, int page, int size);

    // Lấy Lesson theo Course (qua join Section → Course)
    PagedResult<LessonDto> getActiveLessonsByCourseId(UUID courseId, int page, int size);
    PagedResult<LessonWithSolution> getAllLessonsWithSolutionByCourseId(UUID courseId, int page, int size);

    // Lấy tất cả Lesson với bộ lọc
    PagedResult<LessonWithSolution> getAllLessons(int page, int size, String search, UUID courseId, UUID sectionId, Integer type, Boolean requireRobot);

    void reorderLessons(UUID sectionId, ReorderLessonsRequest request);
}
