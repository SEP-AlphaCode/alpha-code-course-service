package site.alphacode.alphacodecourseservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateCategory;
import site.alphacode.alphacodecourseservice.dto.request.patch.PatchCategory;
import site.alphacode.alphacodecourseservice.dto.request.update.UpdateCategory;
import site.alphacode.alphacodecourseservice.dto.response.CategoryDto;
import site.alphacode.alphacodecourseservice.dto.response.PagedResult;
import site.alphacode.alphacodecourseservice.entity.Category;
import site.alphacode.alphacodecourseservice.exception.BadRequestException;
import site.alphacode.alphacodecourseservice.exception.ConflictException;
import site.alphacode.alphacodecourseservice.exception.ResourceNotFoundException;
import site.alphacode.alphacodecourseservice.repository.CategoryRepository;
import site.alphacode.alphacodecourseservice.repository.CourseRepository;
import site.alphacode.alphacodecourseservice.service.S3Service;
import site.alphacode.alphacodecourseservice.service.implement.CategoryServiceImplement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService Unit Tests")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CategoryServiceImplement categoryService;

    private UUID categoryId;
    private Category category;
    private CreateCategory createCategory;
    private UpdateCategory updateCategory;
    private PatchCategory patchCategory;
    private MultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();

        category = Category.builder()
                .id(categoryId)
                .name("Test Category")
                .description("Test Description")
                .slug("test-category")
                .status(1)
                .createdDate(LocalDateTime.now())
                .build();

        multipartFile = mock(MultipartFile.class);

        createCategory = CreateCategory.builder()
                .name("New Category")
                .description("New Description")
                .image(multipartFile)
                .build();

        updateCategory = new UpdateCategory();
        updateCategory.setName("Updated Category");
        updateCategory.setDescription("Updated Description");
        updateCategory.setStatus(1);
        updateCategory.setImageUrl("https://example.com/image.jpg");

        patchCategory = new PatchCategory();
        patchCategory.setName("Patched Category");
        patchCategory.setDescription("Patched Description");
    }

    @Test
    @DisplayName("Should get category by id successfully")
    void testGetCategoryById_Success() {
        // Given
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // When
        CategoryDto result = categoryService.getCategoryById(categoryId);

        // Then
        assertNotNull(result);
        assertEquals(categoryId, result.getId());
        assertEquals("Test Category", result.getName());
        verify(categoryRepository).findById(categoryId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when category not found")
    void testGetCategoryById_NotFound() {
        // Given
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.getCategoryById(categoryId);
        });
        verify(categoryRepository).findById(categoryId);
    }

    @Test
    @DisplayName("Should get category by slug successfully")
    void testGetCategoryBySlug_Success() {
        // Given
        String slug = "test-category";
        // Note: Implementation uses slug.describeConstable() which returns Optional<String>
        // then calls findActiveCategoryBySlug with that Optional
        when(categoryRepository.findActiveCategoryBySlug(anyString())).thenReturn(Optional.of(category));

        // When
        CategoryDto result = categoryService.getCategoryBySlug(slug);

        // Then
        assertNotNull(result);
        assertEquals(slug, result.getSlug());
        verify(categoryRepository).findActiveCategoryBySlug(anyString());
    }

    @Test
    @DisplayName("Should create category successfully")
    void testCreateCategory_Success() throws Exception {
        // Given
        when(categoryRepository.existsByName(createCategory.getName())).thenReturn(false);
        when(multipartFile.getBytes()).thenReturn("image bytes".getBytes());
        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.isEmpty()).thenReturn(false);
        when(s3Service.uploadBytes(any(byte[].class), anyString(), anyString())).thenReturn("https://s3.example.com/image.jpg");

        Category savedCategory = Category.builder()
                .id(categoryId)
                .name(createCategory.getName())
                .description(createCategory.getDescription())
                .slug("new-category")
                .status(1)
                .imageUrl("https://s3.example.com/image.jpg")
                .createdDate(LocalDateTime.now())
                .build();

        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        // When
        CategoryDto result = categoryService.create(createCategory);

        // Then
        assertNotNull(result);
        assertEquals(createCategory.getName(), result.getName());
        assertEquals(createCategory.getDescription(), result.getDescription());
        verify(categoryRepository).existsByName(createCategory.getName());
        verify(categoryRepository).save(any(Category.class));
        verify(s3Service).uploadBytes(any(byte[].class), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw ConflictException when category name already exists")
    void testCreateCategory_NameExists() {
        // Given
        when(categoryRepository.existsByName(createCategory.getName())).thenReturn(true);

        // When & Then
        assertThrows(ConflictException.class, () -> {
            categoryService.create(createCategory);
        });
        verify(categoryRepository).existsByName(createCategory.getName());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Should update category successfully")
    void testUpdateCategory_Success() {
        // Given
        when(categoryRepository.findNoneDeleteCategoryById(categoryId)).thenReturn(Optional.of(category));

        Category updatedCategory = Category.builder()
                .id(categoryId)
                .name(updateCategory.getName())
                .description(updateCategory.getDescription())
                .slug("updated-category")
                .status(updateCategory.getStatus())
                .imageUrl(updateCategory.getImageUrl())
                .createdDate(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();

        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        // When
        CategoryDto result = categoryService.update(categoryId, updateCategory);

        // Then
        assertNotNull(result);
        assertEquals(updateCategory.getName(), result.getName());
        assertEquals(updateCategory.getDescription(), result.getDescription());
        verify(categoryRepository).findNoneDeleteCategoryById(categoryId);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent category")
    void testUpdateCategory_NotFound() {
        // Given
        when(categoryRepository.findNoneDeleteCategoryById(categoryId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.update(categoryId, updateCategory);
        });
        verify(categoryRepository).findNoneDeleteCategoryById(categoryId);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException when updating without image or imageUrl")
    void testUpdateCategory_NoImage() {
        // Given
        updateCategory.setImageUrl(null);
        updateCategory.setImage(null);
        when(categoryRepository.findNoneDeleteCategoryById(categoryId)).thenReturn(Optional.of(category));

        // When & Then
        assertThrows(BadRequestException.class, () -> {
            categoryService.update(categoryId, updateCategory);
        });
        verify(categoryRepository).findNoneDeleteCategoryById(categoryId);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Should patch update category successfully")
    void testPatchCategory_Success() {
        // Given
        when(categoryRepository.findNoneDeleteCategoryById(categoryId)).thenReturn(Optional.of(category));

        Category patchedCategory = Category.builder()
                .id(categoryId)
                .name(patchCategory.getName())
                .description(patchCategory.getDescription())
                .slug("patched-category")
                .status(category.getStatus())
                .imageUrl(category.getImageUrl())
                .createdDate(category.getCreatedDate())
                .lastUpdated(LocalDateTime.now())
                .build();

        when(categoryRepository.save(any(Category.class))).thenReturn(patchedCategory);

        // When
        CategoryDto result = categoryService.patch(categoryId, patchCategory);

        // Then
        assertNotNull(result);
        assertEquals(patchCategory.getName(), result.getName());
        assertEquals(patchCategory.getDescription(), result.getDescription());
        verify(categoryRepository).findNoneDeleteCategoryById(categoryId);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when patching non-existent category")
    void testPatchCategory_NotFound() {
        // Given
        when(categoryRepository.findNoneDeleteCategoryById(categoryId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.patch(categoryId, patchCategory);
        });
        verify(categoryRepository).findNoneDeleteCategoryById(categoryId);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Should delete category successfully")
    void testDeleteCategory_Success() {
        // Given
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).softDeleteById(categoryId);

        // When
        assertDoesNotThrow(() -> {
            categoryService.delete(categoryId);
        });

        // Then
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).softDeleteById(categoryId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent category")
    void testDeleteCategory_NotFound() {
        // Given
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.delete(categoryId);
        });
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository, never()).softDeleteById(any(UUID.class));
    }
}

