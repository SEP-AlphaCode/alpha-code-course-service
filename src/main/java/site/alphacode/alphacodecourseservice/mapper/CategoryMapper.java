package site.alphacode.alphacodecourseservice.mapper;

import site.alphacode.alphacodecourseservice.dto.response.CategoryDto;
import site.alphacode.alphacodecourseservice.entity.Category;

public class CategoryMapper {
    public static CategoryDto toDto(Category category) {
        if (category == null) return null;

        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setCreatedDate(category.getCreatedDate());
        dto.setLastUpdated(category.getLastUpdated());
        dto.setSlug(category.getSlug());
        dto.setImageUrl(category.getImageUrl());
        dto.setStatus(category.getStatus());
        return dto;
    }

    public static Category toEntity(CategoryDto dto) {
        if (dto == null) return null;

        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setCreatedDate(dto.getCreatedDate());
        category.setLastUpdated(dto.getLastUpdated());
        category.setSlug(dto.getSlug());
        category.setImageUrl(dto.getImageUrl());
        category.setStatus(dto.getStatus());

        return category;
    }
}
