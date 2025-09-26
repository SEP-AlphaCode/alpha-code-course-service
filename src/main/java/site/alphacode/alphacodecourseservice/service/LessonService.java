package site.alphacode.alphacodecourseservice.service;

import site.alphacode.alphacodecourseservice.dto.request.create.CreateLesson;
import site.alphacode.alphacodecourseservice.dto.request.patch.PatchLesson;
import site.alphacode.alphacodecourseservice.dto.request.update.UpdateLesson;
import site.alphacode.alphacodecourseservice.dto.response.LessonDto;
import site.alphacode.alphacodecourseservice.dto.response.LessonWithSolution;

import java.util.UUID;

public interface LessonService {
    LessonDto getLessonById(UUID id);
    LessonWithSolution getLessonWithSolutionById(UUID id);
    LessonWithSolution create (CreateLesson createLesson);
    LessonWithSolution update(UUID lessonId, UpdateLesson updateLesson);
    LessonWithSolution patch(UUID lessonId, PatchLesson patchLesson);
    void delete(UUID lessonId);

}
