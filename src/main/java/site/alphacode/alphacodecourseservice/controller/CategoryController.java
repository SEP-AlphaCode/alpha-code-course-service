package site.alphacode.alphacodecourseservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import site.alphacode.alphacodecourseservice.dto.CategoryDto;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateCategory;
import site.alphacode.alphacodecourseservice.dto.request.update.UpdateCategory;
import site.alphacode.alphacodecourseservice.service.CategoryService;
import site.alphacode.alphacodecourseservice.validation.OnPut;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create predefined category")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public CategoryDto createCategory(
            @Validated @ModelAttribute CreateCategory createCategory) {
        return categoryService.create(createCategory);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update category (replace all fields)")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public CategoryDto updateCategory(
            @PathVariable UUID id,
            @Validated(OnPut.class) @ModelAttribute UpdateCategory updateCategory) {
        return categoryService.update(id, updateCategory);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Patch category (partial update)")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public CategoryDto patchCategory(
            @PathVariable UUID id,
            @ModelAttribute UpdateCategory updateCategory) {
        return categoryService.patch(id, updateCategory);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category by id")
    @PreAuthorize("hasAuthority('ROLE_Admin')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable UUID id) {
        categoryService.delete(id);
    }

}
