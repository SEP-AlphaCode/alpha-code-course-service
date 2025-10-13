package site.alphacode.alphacodecourseservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateCourseBundle;
import site.alphacode.alphacodecourseservice.dto.request.patch.PatchCourseBundle;
import site.alphacode.alphacodecourseservice.dto.request.update.UpdateCourseBundle;
import site.alphacode.alphacodecourseservice.dto.response.CourseBundleDto;
import site.alphacode.alphacodecourseservice.dto.response.CourseDto;
import site.alphacode.alphacodecourseservice.service.CourseBundleService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/course-bundles")
@RequiredArgsConstructor
@Tag(name = "Course Bundles", description = "Course Bundle management APIs")
public class CourseBundleController {
    private final CourseBundleService courseBundleService;

    @GetMapping("/get-all-course-by-bundle/{bundleId}")
    @Operation(summary = "Get all courses by bundle ID")
    public List<CourseDto> getAllCourseByBundleId(@PathVariable UUID bundleId) {
        return courseBundleService.courseBundle(bundleId);
    }

    @PostMapping
    @Operation(summary = "Assign course to bundle")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public CourseBundleDto create(@RequestBody CreateCourseBundle request) {
        return courseBundleService.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update course bundle by ID")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public CourseBundleDto update(@PathVariable UUID id, UpdateCourseBundle request) {
        return courseBundleService.update(id, request);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Patch update course bundle by ID")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public CourseBundleDto patch(@PathVariable UUID id, PatchCourseBundle request) {
        return courseBundleService.patch(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete course bundle by ID")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public void delete(@PathVariable UUID id) {
        courseBundleService.delete(id);
    }
}
