package site.alphacode.alphacodecourseservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import site.alphacode.alphacodecourseservice.dto.request.ReorderLessonsRequest;
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

    // ------------------- GET -------------------

    @GetMapping("/{id}")
    @Operation(summary = "Get active lesson by id")
    public LessonDto getById(@PathVariable UUID id) {
        return lessonService.getLessonById(id);
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get active lesson by slug")
    public LessonDto getBySlug(@PathVariable String slug) {
        return lessonService.getLessonBySlug(slug);
    }

    @GetMapping("/with-solution/{id}")
    @Operation(summary = "Get lesson with solution by id (Admin and Staff only)")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public LessonWithSolution getLessonWithSolutionById(@PathVariable UUID id) {
        return lessonService.getLessonWithSolutionById(id);
    }

    @GetMapping("/with-solution/slug/{slug}")
    @Operation(summary = "Get lesson with solution by slug (Admin and Staff only)")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public LessonWithSolution getLessonWithSolutionBySlug(@PathVariable String slug) {
        return lessonService.getLessonWithSolutionBySlug(slug);
    }

    @GetMapping("/get-by-course/{courseId}")
    @Operation(summary = "Get all active lessons by course id")
    public PagedResult<LessonDto> getActiveLessonsByCourseId(
            @PathVariable UUID courseId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return lessonService.getActiveLessonsByCourseId(courseId, page, size);
    }

    @GetMapping("/all-with-solution-by-course/{courseId}")
    @Operation(summary = "Get all lessons with solutions by course id (Admin and Staff only)")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public PagedResult<LessonWithSolution> getAllLessonsWithSolutionsByCourseId(
            @PathVariable UUID courseId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return lessonService.getAllLessonsWithSolutionByCourseId(courseId, page, size);
    }

    @GetMapping("/get-by-section/{sectionId}")
    @Operation(summary = "Get all active lessons by section id")
    public PagedResult<LessonDto> getActiveLessonsBySectionId(
            @PathVariable UUID sectionId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return lessonService.getActiveLessonsBySectionId(sectionId, page, size);
    }

    @GetMapping("/all-with-solution-by-section/{sectionId}")
    @Operation(summary = "Get all lessons with solutions by section id (Admin and Staff only)")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public PagedResult<LessonWithSolution> getAllLessonsWithSolutionsBySectionId(
            @PathVariable UUID sectionId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return lessonService.getAllLessonsWithSolutionBySectionId(sectionId, page, size);
    }

    @GetMapping
    @Operation(summary = "Get all lessons with filters (Admin and Staff only)")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public PagedResult<LessonWithSolution> getAllLessons(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) UUID courseId,
            @RequestParam(required = false) UUID sectionId,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Boolean requireRobot
    ) {
        return lessonService.getAllLessons(page, size, search, courseId, sectionId, type, requireRobot);
    }

    // ------------------- CREATE -------------------

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a new lesson (Admin and Staff only)")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public LessonWithSolution create(
            @Valid @RequestPart("createLesson")  CreateLesson createLesson,
            @RequestPart(value = "videoFile", required = false) MultipartFile videoFile
    ) {
        return lessonService.create(createLesson, videoFile);
    }

    // ------------------- UPDATE -------------------

    // ------------------- UPDATE -------------------
    @PutMapping(value = "/{lessonId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update a lesson (Admin and Staff only)")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public LessonWithSolution update(
            @PathVariable UUID lessonId,
            @Valid @RequestPart("updateLesson") UpdateLesson updateLesson,
            @RequestPart(value = "videoFile", required = false) MultipartFile videoFile
    ) {
        return lessonService.update(lessonId, updateLesson, videoFile);
    }

    // ------------------- PATCH -------------------
    @PatchMapping(value = "/{lessonId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Patch a lesson (Admin and Staff only)")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public LessonWithSolution patch(
            @PathVariable UUID lessonId,
            @Valid @RequestPart("patchLesson") PatchLesson patchLesson,
            @RequestPart(value = "videoFile", required = false) MultipartFile videoFile
    ) {
        return lessonService.patch(lessonId, patchLesson, videoFile);
    }


    // ------------------- DELETE -------------------

    @DeleteMapping("/{lessonId}")
    @Operation(summary = "Delete a lesson (Admin and Staff only)")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public void delete(@PathVariable UUID lessonId) {
        lessonService.delete(lessonId);
    }

    @PutMapping("/{sectionId}/lessons/reorder")
    @Operation(summary = "Reorder lessons within section or move between sections (Admin and Staff only)")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public void reorderLessons(
            @PathVariable UUID sectionId,
            @Valid @RequestBody ReorderLessonsRequest request
    ) {
        lessonService.reorderLessons(sectionId, request);
    }
}
