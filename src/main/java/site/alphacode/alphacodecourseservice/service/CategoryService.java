package site.alphacode.alphacodecourseservice.service;

import site.alphacode.alphacodecourseservice.dto.CategoryDto;
import site.alphacode.alphacodecourseservice.dto.PagedResult;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateCategory;
import site.alphacode.alphacodecourseservice.dto.request.patch.PatchCategory;
import site.alphacode.alphacodecourseservice.dto.request.update.UpdateCategory;

import java.util.UUID;

public interface CategoryService {
    CategoryDto getCategoryById(UUID id);
    PagedResult<CategoryDto> getCategories(int page, int size, String search);
    CategoryDto update(UUID id, UpdateCategory updateCategory);
    CategoryDto create(CreateCategory createCategory);
    CategoryDto patch(UUID id, PatchCategory patchCategory);
    void delete(UUID id);
    CategoryDto getCategoryBySlug(String slug);
}
