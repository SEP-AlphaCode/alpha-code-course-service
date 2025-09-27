package site.alphacode.alphacodecourseservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import site.alphacode.alphacodecourseservice.dto.response.CategoryDto;
import site.alphacode.alphacodecourseservice.dto.response.PagedResult;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateCategory;
import site.alphacode.alphacodecourseservice.dto.request.patch.PatchCategory;
import site.alphacode.alphacodecourseservice.dto.request.update.UpdateCategory;
import site.alphacode.alphacodecourseservice.service.CategoryService;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Category management APIs")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/{id}")
    @Operation(summary = "Get category by id")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public CategoryDto getCategoryById(@PathVariable UUID id) {
        return categoryService.getCategoryById(id);
    }

    @GetMapping("/get-by-slug/{slug}")
    @Operation(summary = "Get category by slug")
    public CategoryDto getCategoryBySlug(@PathVariable String slug) {
        return categoryService.getCategoryBySlug(slug);
    }

    @GetMapping
    @Operation(summary = "Get list of categories with paging & search")
    public PagedResult<CategoryDto> getAllCategories(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        return categoryService.getCategories(page, size, search);
    }

    @GetMapping("/none-delete")
    @Operation(summary = "Get list of none-deleted categories with paging & search")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public PagedResult<CategoryDto> getAllNoneDeleteCategories(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        return categoryService.getNoneDeleteCategories(page, size, search);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create predefined category")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public CategoryDto createCategory(
            @Valid @ModelAttribute CreateCategory createCategory) {
        return categoryService.create(createCategory);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update category (replace all fields)")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public CategoryDto updateCategory(
            @PathVariable UUID id,
            @Valid @ModelAttribute UpdateCategory updateCategory) {
        return categoryService.update(id, updateCategory);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Patch category (partial update)")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public CategoryDto patchCategory(
            @PathVariable UUID id,
            @Valid @ModelAttribute PatchCategory patchCategory) {
        return categoryService.patch(id, patchCategory);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category by id")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable UUID id) {
        categoryService.delete(id);
    }

}
