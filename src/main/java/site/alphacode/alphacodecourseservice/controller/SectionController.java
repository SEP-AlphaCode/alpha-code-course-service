package site.alphacode.alphacodecourseservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateSection;
import site.alphacode.alphacodecourseservice.dto.request.update.UpdateSection;
import site.alphacode.alphacodecourseservice.dto.response.PagedResult;
import site.alphacode.alphacodecourseservice.dto.response.SectionDto;
import site.alphacode.alphacodecourseservice.service.SectionService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sections")
@RequiredArgsConstructor
@Tag(name = "Sections", description = "Section management APIs")
public class SectionController {

    private final SectionService sectionService;

    @GetMapping("/{id}")
    @Operation(summary = "Get section by id")
    public SectionDto getById(@PathVariable UUID id) {
        return sectionService.getById(id);
    }

    @GetMapping("/get-by-course/{courseId}")
    @Operation(summary = "Get all sections by course id")
    public PagedResult<SectionDto> getAllByCourseId(
            @PathVariable UUID courseId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return sectionService.getAllByCourseId(courseId, page, size);
    }

    @PostMapping
    @Operation(summary = "Create a new section (Admin and Staff only)")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public SectionDto create(@Valid @RequestBody CreateSection createSection) {
        return sectionService.create(createSection);
    }

    @PutMapping("/{sectionId}")
    @Operation(summary = "Update a section (Admin and Staff only)")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public SectionDto update(@PathVariable UUID sectionId, @Valid @RequestBody UpdateSection updateSection) {
        return sectionService.update(sectionId, updateSection);
    }

    @DeleteMapping("/{sectionId}")
    @Operation(summary = "Delete a section (Admin and Staff only)")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public void delete(@PathVariable UUID sectionId) {
        sectionService.delete(sectionId);
    }
}
