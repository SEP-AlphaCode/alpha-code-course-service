package site.alphacode.alphacodecourseservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import site.alphacode.alphacodecourseservice.dto.CourseDto;
import site.alphacode.alphacodecourseservice.dto.PagedResult;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateCourse;
import site.alphacode.alphacodecourseservice.dto.request.patch.PatchCourse;
import site.alphacode.alphacodecourseservice.dto.request.update.UpdateCourse;
import site.alphacode.alphacodecourseservice.service.CourseService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Tag(name = "Courses")
public class CourseController {
    private final CourseService courseService;

    @GetMapping("/{id}")
    @Operation(summary = "Get active course by id")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public CourseDto getActiveCourseById(UUID id) {
       return courseService.getActiveCourseById(id);
    }

    @GetMapping
    @Operation(summary = "Get all active courses with pagination and optional search filter")
    public PagedResult<CourseDto> getAllActiveCourses(@RequestParam(defaultValue = "1") int page,@RequestParam(defaultValue = "10") int size,@RequestParam(required = false) String search) {
        return courseService.getAllActiveCourses(page, size, search);
    }

    @GetMapping("/get-by-slug/{slug}")
    @Operation(summary = "Get active course by slug")
    public CourseDto getActiveCourseBySlug(@PathVariable String slug) {
        return courseService.getActiveCourseBySlug(slug);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    @Operation(summary = "Create new course")
    public CourseDto create( @Valid @ModelAttribute @RequestBody CreateCourse createCourse) {
        return courseService.create(createCourse);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    @Operation(summary = "Update course by id")
    public CourseDto update(@RequestParam UUID id, @Valid @ModelAttribute @RequestBody UpdateCourse updateCourse) {
        return courseService.update(id, updateCourse);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    @Operation(summary = "Patch update course by id")
    public CourseDto patchUpdate(@RequestParam UUID id, @Valid @ModelAttribute @RequestBody PatchCourse patchCourse) {
        return courseService.patchUpdate(id, patchCourse);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    @Operation(summary = "Delete course by id")
    public void delete(@PathVariable UUID id) {
        courseService.delete(id);
    }
}
