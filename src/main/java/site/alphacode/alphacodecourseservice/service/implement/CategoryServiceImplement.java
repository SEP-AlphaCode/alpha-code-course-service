package site.alphacode.alphacodecourseservice.service.implement;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import site.alphacode.alphacodecourseservice.dto.response.CategoryDto;
import site.alphacode.alphacodecourseservice.dto.response.PagedResult;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateCategory;
import site.alphacode.alphacodecourseservice.dto.request.patch.PatchCategory;
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

    @Override
    @Cacheable(value = "category", key = "{#slug}")
    public CategoryDto getCategoryBySlug(String slug) {
        var category = categoryRepository.findActiveCategoryBySlug((slug)
                .describeConstable().orElseThrow(() -> new ResourceNotFoundException("Danh muc với slug: " + slug + " không tìm thấy")));
        return CategoryMapper.toDto(category.get());
    }

    @Override
    @Cacheable(value = "category", key = "{#id}")
    public CategoryDto getCategoryById(UUID id) {
        var category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Danh mục với id: " + id + " không tìm thấy"));
        return CategoryMapper.toDto(category);
    }

    @Override
    @Cacheable(value = "categories_list", key = "{#page, #size, #search}")
    public PagedResult<CategoryDto> getCategories(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdDate").descending());

        Page<Category> categoryPage = categoryRepository.findAllActiveCategories(search, pageable);

        return new PagedResult<>(categoryPage.map(CategoryMapper::toDto));
    }

    @Override
    @Cacheable(value = "none_delete_categories_list", key = "{#page, #size, #search}")
    public PagedResult<CategoryDto> getNoneDeleteCategories(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdDate").descending());

        Page<Category> categoryPage = categoryRepository.findAllActiveCategories(search, pageable);

        return new PagedResult<>(categoryPage.map(CategoryMapper::toDto));
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "categories_list", allEntries = true),  // xóa toàn bộ danh sách
                    @CacheEvict(value = "none_delete_categories_list", allEntries = true),  // xóa toàn bộ danh sách
                    @CacheEvict(value = "category", key = "{#id}")                // xóa chi tiết theo id
            }
    )
    public void delete(UUID id) {
        categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Danh mục với id: " + id + " không tìm thấy"));
        categoryRepository.softDeleteById(id);
    }

    @Override
    @Transactional
    @CachePut(value = "category", key = "{#result.id}") // thêm mới thì put vào cache theo id
    @Caching(
            evict = {
                    @CacheEvict(value = "categories_list", allEntries = true),  // xóa toàn bộ danh sách
                    @CacheEvict(value = "none_delete_categories_list", allEntries = true)  // xóa toàn bộ danh sách
            }
    )
    public CategoryDto create(CreateCategory createCategory) {
        if (categoryRepository.existsByName(createCategory.getName())) {
            throw new ConflictException("Danh mục với tên " + createCategory.getName() + " đã tồn tại.");
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
                String imageUrl = s3Service.uploadBytes(createCategory.getImage().getBytes(), fileKey, createCategory.getImage().getContentType());
                category.setImageUrl(imageUrl);
            }

            Category savedEntity = categoryRepository.save(category);
            return CategoryMapper.toDto(savedEntity);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo Category", e);
        }
    }

    @Override
    @Transactional
    @CachePut(value = "category", key = "{#result.id}") // update thì update lại cache theo id
    @Caching(
            evict = {
                    @CacheEvict(value = "categories_list", allEntries = true),  // xóa toàn bộ danh sách
                    @CacheEvict(value = "none_delete_categories_list", allEntries = true)  // xóa toàn bộ danh sách
            }
    )
    public CategoryDto update(UUID id, UpdateCategory updateCategory) {
        Category existingCategory = categoryRepository.findNoneDeleteCategoryById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Danh mục với id: " + id + " không tìm thấy"));

        existingCategory.setName(updateCategory.getName());
        existingCategory.setDescription(updateCategory.getDescription());
        existingCategory.setStatus(updateCategory.getStatus());
        existingCategory.setLastUpdated(LocalDateTime.now());

        // Xử lý ảnh (PUT => bắt buộc có image file hoặc imageUrl)
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
            throw new BadRequestException("Chỉnh sửa danh mục phải có ảnh (image file hoặc imageUrl)");
        }

        Category savedEntity = categoryRepository.save(existingCategory);
        return CategoryMapper.toDto(savedEntity);
    }

    @Override
    @Transactional
    @CachePut(value = "category", key = "{#id}") // patch cũng update cache
    @Caching(
            evict = {
                    @CacheEvict(value = "categories_list", allEntries = true),  // xóa toàn bộ danh sách
                    @CacheEvict(value = "none_delete_categories_list", allEntries = true)  // xóa toàn bộ danh sách
            }
    )
    public CategoryDto patch(UUID id, PatchCategory patchCategory) {
        Category existingCategory = categoryRepository.findNoneDeleteCategoryById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Danh mục với idL: " + id + " không tìm thấy"));

        if (patchCategory.getName() != null) {
            existingCategory.setName(patchCategory.getName());
        }
        if (patchCategory.getDescription() != null) {
            existingCategory.setDescription(patchCategory.getDescription());
        }
        if (patchCategory.getStatus() != null) {
            existingCategory.setStatus(patchCategory.getStatus());
        }

        if (patchCategory.getImage() != null && !patchCategory.getImage().isEmpty()) {
            try {
                String fileKey = "categories/" + patchCategory.getImage().getOriginalFilename();
                String imageUrl = s3Service.uploadBytes(
                        patchCategory.getImage().getBytes(),
                        fileKey,
                        patchCategory.getImage().getContentType()
                );
                existingCategory.setImageUrl(imageUrl);
            } catch (Exception e) {
                throw new RuntimeException("Lỗi khi tải lên ảnh", e);
            }
        } else if (patchCategory.getImageUrl() != null && !patchCategory.getImageUrl().isBlank()) {
            existingCategory.setImageUrl(patchCategory.getImageUrl());
        }

        existingCategory.setLastUpdated(LocalDateTime.now());

        Category savedEntity = categoryRepository.save(existingCategory);
        return CategoryMapper.toDto(savedEntity);
    }
}

