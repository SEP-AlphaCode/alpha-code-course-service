package site.alphacode.alphacodecourseservice.service.implement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.alphacode.alphacodecourseservice.dto.CategoryDto;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateCategory;
import site.alphacode.alphacodecourseservice.dto.request.update.UpdateCategory;
import site.alphacode.alphacodecourseservice.entity.Category;
import site.alphacode.alphacodecourseservice.exception.BadRequestException;
import site.alphacode.alphacodecourseservice.exception.ConflictException;
import site.alphacode.alphacodecourseservice.exception.ResourceNotFoundException;
import site.alphacode.alphacodecourseservice.mapper.CategoryMapper;
import site.alphacode.alphacodecourseservice.repository.CategoryRepository;
import site.alphacode.alphacodecourseservice.service.CategoryService;
import site.alphacode.alphacodecourseservice.service.S3Service;
import site.alphacode.alphacodecourseservice.util.SlugHelper;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImplement implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final S3Service s3Service;

    public void delete(UUID id){
        categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id " + id + " not found"));
        categoryRepository.softDeleteById(id);
    }

    public CategoryDto create(CreateCategory createCategory) {
        if(categoryRepository.existsByName(createCategory.getName())) {
            throw new ConflictException("Category with name " + createCategory.getName() + " already exists");
        }

        Category category = new Category();
        category.setSlug(SlugHelper.toSlug(createCategory.getName()));
        category.setName(createCategory.getName());
        category.setDescription(createCategory.getDescription());
        category.setCreatedDate(LocalDateTime.now());
        category.setLastUpdated(null);
        category.setStatus(1);
        try {
            if (createCategory.getImage() != null && !createCategory.getImage().isEmpty()) {
                String fileKey = "categories/" + createCategory.getImage().getOriginalFilename();
                String avatarUrl = s3Service.uploadBytes(createCategory.getImage().getBytes(), fileKey, createCategory.getImage().getContentType());
                category.setImageUrl(avatarUrl);
            }

            Category savedEntity = categoryRepository.save(category);
            return CategoryMapper.toDto(savedEntity);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo Account", e);
        }
    }

    public CategoryDto update(UUID id, UpdateCategory updateCategory) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id " + id + " not found"));

        existingCategory.setName(updateCategory.getName());
        existingCategory.setDescription(updateCategory.getDescription());
        existingCategory.setStatus(updateCategory.getStatus());
        existingCategory.setLastUpdated(LocalDateTime.now());

        // Xử lý ảnh (PUT => bắt buộc phải có hoặc image file, hoặc imageUrl string)
        if (updateCategory.getImage() != null && !updateCategory.getImage().isEmpty()) {
            try {
                String fileKey = "categories/" + updateCategory.getImage().getOriginalFilename();
                String imageUrl = s3Service.uploadBytes(
                        updateCategory.getImage().getBytes(),
                        fileKey,
                        updateCategory.getImage().getContentType()
                );
                existingCategory.setImageUrl(imageUrl);
            } catch (Exception e) {
                throw new RuntimeException("Lỗi khi tải lên ảnh", e);
            }
        } else if (updateCategory.getImageUrl() != null && !updateCategory.getImageUrl().isBlank()) {
            existingCategory.setImageUrl(updateCategory.getImageUrl());
        } else {
            throw new BadRequestException("PUT category phải có ảnh (image file hoặc imageUrl)");
        }

        Category savedEntity = categoryRepository.save(existingCategory);
        return CategoryMapper.toDto(savedEntity);
    }


    public CategoryDto patch(UUID id, UpdateCategory updateCategory) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id " + id + " not found"));

        if (updateCategory.getName() != null) {
            existingCategory.setName(updateCategory.getName());
        }
        if (updateCategory.getDescription() != null) {
            existingCategory.setDescription(updateCategory.getDescription());
        }
        if (updateCategory.getStatus() != null) {
            existingCategory.setStatus(updateCategory.getStatus());
        }

        if (updateCategory.getImage() != null && !updateCategory.getImage().isEmpty()) {
            try {
                String fileKey = "categories/" + updateCategory.getImage().getOriginalFilename();
                String imageUrl = s3Service.uploadBytes(
                        updateCategory.getImage().getBytes(),
                        fileKey,
                        updateCategory.getImage().getContentType()
                );
                existingCategory.setImageUrl(imageUrl);
            } catch (Exception e) {
                throw new RuntimeException("Lỗi khi tải lên ảnh", e);
            }
        } else if (updateCategory.getImageUrl() != null && !updateCategory.getImageUrl().isBlank()) {
            // Nếu client gửi lại URL cũ hoặc URL mới
            existingCategory.setImageUrl(updateCategory.getImageUrl());
        }

        existingCategory.setLastUpdated(LocalDateTime.now());

        Category savedEntity = categoryRepository.save(existingCategory);
        return CategoryMapper.toDto(savedEntity);
    }

}
