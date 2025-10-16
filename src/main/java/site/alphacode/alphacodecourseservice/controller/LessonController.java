package site.alphacode.alphacodecourseservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
    public PagedResult<LessonDto> getActiveLessonsByCourseId(@PathVariable UUID courseId,@RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "10") Integer size) {
        return lessonService.getActiveLessonsByCourseId(courseId, page, size);
    }

    @GetMapping("/all-with-solution-by-course/{courseId}")
    @Operation(summary = "Get all lessons with solutions by course id (Admin and Staff only)")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public PagedResult<LessonWithSolution> getAllLessonsWithSolutionsByCourseId(@PathVariable UUID courseId,@RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "10") Integer size) {
        return lessonService.getAllLessonsWithSolutionByCourseId(courseId, page, size);
    }

    @GetMapping("/with-solution/{id}")
    @Operation(summary = "Get lesson with solution by id (Admin and Staff only)")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public LessonWithSolution getLessonWithSolutionById(@PathVariable UUID id) {
        return lessonService.getLessonWithSolutionById(id);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a new lesson (Admin and Staff only)")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public LessonWithSolution create(
            @RequestPart("data") String dataJson,
            @RequestPart(value = "videoFile", required = false) MultipartFile videoFile
    ) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        CreateLesson createLesson = mapper.readValue(dataJson, CreateLesson.class);
        createLesson.setVideoFile(videoFile);
        return lessonService.create(createLesson);
    }

    @PutMapping(value = "/{lessonId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update a lesson (Admin and Staff only)")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public LessonWithSolution update(
            @PathVariable UUID lessonId,
            @RequestParam @Valid UpdateLesson updateLesson,
            @RequestPart(value = "videoFile", required = false) MultipartFile videoFile
    ) {
        updateLesson.setVideoFile(videoFile);
        return lessonService.update(lessonId, updateLesson);
    }

    @PatchMapping(value = "/{lessonId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Patch a lesson (Admin and Staff only)")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public LessonWithSolution patch(
            @PathVariable UUID lessonId,
            @RequestParam @Valid PatchLesson patchLesson,
            @RequestPart(value = "videoFile", required = false) MultipartFile videoFile
    ) {
        patchLesson.setVideoFile(videoFile);
        return lessonService.patch(lessonId, patchLesson);
    }

    @DeleteMapping("/{lessonId}")
    @Operation(summary = "Delete a lesson (Admin and Staff only)")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public void delete(@PathVariable UUID lessonId) {
        lessonService.delete(lessonId);
    }
}
