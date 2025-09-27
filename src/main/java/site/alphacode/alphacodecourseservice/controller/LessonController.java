package site.alphacode.alphacodecourseservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateLesson;
import site.alphacode.alphacodecourseservice.dto.request.patch.PatchLesson;
import site.alphacode.alphacodecourseservice.dto.request.update.UpdateLesson;
import site.alphacode.alphacodecourseservice.dto.response.LessonDto;
import site.alphacode.alphacodecourseservice.dto.response.LessonWithSolution;
import site.alphacode.alphacodecourseservice.dto.response.PagedResult;
import site.alphacode.alphacodecourseservice.service.LessonService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/lessons")
@RequiredArgsConstructor
@Tag(name = "Lessons", description = "Lesson management APIs")
public class LessonController {
    private final LessonService lessonService;

    @GetMapping("/{id}")
    @Operation(summary = "Get active lesson by id")
    public LessonDto getById(@PathVariable UUID id) {
        return lessonService.getLessonById(id);
    }

    @GetMapping("/get-by-course/{courseId}")
    @Operation(summary = "Get all active lessons by course id")
    public PagedResult<LessonDto> getActiveLessonsByCourseId(@PathVariable UUID courseId, Integer page, Integer size) {
        return lessonService.getActiveLessonsByCourseId(courseId, page, size);
    }

    @GetMapping("/all-with-solution-by-course/{courseId}")
    @Operation(summary = "Get all lessons with solutions by course id (Admin and Staff only)")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public PagedResult<LessonWithSolution> getAllLessonsWithSolutionsByCourseId(@PathVariable UUID courseId, Integer page, Integer size) {
        return lessonService.getAllLessonsWithSolutionByCourseId(courseId, page, size);
    }

    @GetMapping("/with-solution/{id}")
    @Operation(summary = "Get lesson with solution by id (Admin and Staff only)")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public LessonWithSolution getLessonWithSolutionById(@PathVariable UUID id) {
        return lessonService.getLessonWithSolutionById(id);
    }

    @PostMapping
    @Operation(summary = "Create a new lesson (Admin and Staff only)")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public LessonWithSolution create(@RequestBody CreateLesson createLesson) {
        return lessonService.create(createLesson);
    }

    @PutMapping("/{lessonId}")
    @Operation(summary = "Update a lesson (Admin and Staff only)")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public LessonWithSolution update(@PathVariable UUID lessonId, @RequestBody UpdateLesson updateLesson) {
        return lessonService.update(lessonId, updateLesson);
    }

    @PatchMapping("/{lessonId}")
    @Operation(summary = "Patch a lesson (Admin and Staff only)")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public LessonWithSolution patch(@PathVariable UUID lessonId, @RequestBody PatchLesson patchLesson) {
        return lessonService.patch(lessonId, patchLesson);
    }

    @DeleteMapping("/{lessonId}")
    @Operation(summary = "Delete a lesson (Admin and Staff only)")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public void delete(@PathVariable UUID lessonId) {
        lessonService.delete(lessonId);
    }
}
