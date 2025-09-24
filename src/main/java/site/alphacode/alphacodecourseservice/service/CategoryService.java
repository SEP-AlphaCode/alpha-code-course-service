package site.alphacode.alphacodecourseservice.service;

import site.alphacode.alphacodecourseservice.dto.CategoryDto;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateCategory;
import site.alphacode.alphacodecourseservice.dto.request.update.UpdateCategory;

import java.util.UUID;

public interface CategoryService {
    CategoryDto update(UUID id, UpdateCategory updateCategory);
    CategoryDto create(CreateCategory createCategory);
    CategoryDto patch(UUID id, UpdateCategory updateCategory);
    void delete(UUID id);
}
