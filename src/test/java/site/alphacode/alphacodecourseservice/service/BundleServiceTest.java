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
import site.alphacode.alphacodecourseservice.dto.request.create.CreateBundle;
import site.alphacode.alphacodecourseservice.dto.request.patch.PatchBundle;
import site.alphacode.alphacodecourseservice.dto.request.update.UpdateBundle;
import site.alphacode.alphacodecourseservice.dto.response.BundleDto;
import site.alphacode.alphacodecourseservice.dto.response.PagedResult;
import site.alphacode.alphacodecourseservice.entity.Bundle;
import site.alphacode.alphacodecourseservice.entity.Course;
import site.alphacode.alphacodecourseservice.entity.CourseBundle;
import site.alphacode.alphacodecourseservice.exception.BadRequestException;
import site.alphacode.alphacodecourseservice.exception.ConflictException;
import site.alphacode.alphacodecourseservice.repository.BundleRepository;
import site.alphacode.alphacodecourseservice.service.S3Service;
import site.alphacode.alphacodecourseservice.service.implement.BundleServiceImplement;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BundleService Unit Tests")
class BundleServiceTest {

    @Mock
    private BundleRepository bundleRepository;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private BundleServiceImplement bundleService;

    private UUID bundleId;
    private Bundle bundle;
    private CreateBundle createBundle;
    private UpdateBundle updateBundle;
    private PatchBundle patchBundle;
    private MultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        bundleId = UUID.randomUUID();

        bundle = Bundle.builder()
                .id(bundleId)
                .name("Test Bundle")
                .description("Test Description")
                .price(500000)
                .discountPrice(400000)
                .coverImage("https://example.com/image.jpg")
                .status(1)
                .createdDate(LocalDateTime.now())
                .build();

        multipartFile = mock(MultipartFile.class);

        createBundle = CreateBundle.builder()
                .name("New Bundle")
                .description("New Description")
                .price(600000)
                .discountPrice(500000)
                .coverImage(multipartFile)
                .build();

        updateBundle = new UpdateBundle();
        updateBundle.setId(bundleId);
        updateBundle.setName("Updated Bundle");
        updateBundle.setDescription("Updated Description");
        updateBundle.setPrice(700000);
        updateBundle.setDiscountPrice(600000);
        updateBundle.setStatus(1);
        updateBundle.setCoverImage("https://example.com/updated.jpg");

        patchBundle = new PatchBundle();
        patchBundle.setName("Patched Bundle");
        patchBundle.setPrice(650000);
        patchBundle.setDiscountPrice(550000);
    }

    @Test
    @DisplayName("Should get bundle by id successfully")
    void testGetById_Success() {
        // Given
        when(bundleRepository.findNoneDeleteById(bundleId)).thenReturn(Optional.of(bundle));

        // When
        BundleDto result = bundleService.getById(bundleId);

        // Then
        assertNotNull(result);
        assertEquals(bundleId, result.getId());
        assertEquals("Test Bundle", result.getName());
        verify(bundleRepository).findNoneDeleteById(bundleId);
    }

    @Test
    @DisplayName("Should throw BadRequestException when bundle not found")
    void testGetById_NotFound() {
        // Given
        when(bundleRepository.findNoneDeleteById(bundleId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BadRequestException.class, () -> {
            bundleService.getById(bundleId);
        });
        verify(bundleRepository).findNoneDeleteById(bundleId);
    }

    @Test
    @DisplayName("Should get active bundle by id successfully")
    void testGetActiveById_Success() {
        // Given
        when(bundleRepository.findByIdAndStatus(bundleId, 1)).thenReturn(Optional.of(bundle));

        // When
        BundleDto result = bundleService.getActiveById(bundleId);

        // Then
        assertNotNull(result);
        assertEquals(bundleId, result.getId());
        verify(bundleRepository).findByIdAndStatus(bundleId, 1);
    }

    @Test
    @DisplayName("Should throw BadRequestException when active bundle not found")
    void testGetActiveById_NotFound() {
        // Given
        when(bundleRepository.findByIdAndStatus(bundleId, 1)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BadRequestException.class, () -> {
            bundleService.getActiveById(bundleId);
        });
        verify(bundleRepository).findByIdAndStatus(bundleId, 1);
    }

    @Test
    @DisplayName("Should create bundle successfully")
    void testCreateBundle_Success() throws Exception {
        // Given
        when(bundleRepository.findByName(createBundle.getName())).thenReturn(Optional.empty());
        when(multipartFile.getBytes()).thenReturn("image bytes".getBytes());
        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.isEmpty()).thenReturn(false);
        when(s3Service.uploadBytes(any(byte[].class), anyString(), anyString())).thenReturn("https://s3.example.com/image.jpg");

        Bundle savedBundle = Bundle.builder()
                .id(bundleId)
                .name(createBundle.getName())
                .description(createBundle.getDescription())
                .price(createBundle.getPrice())
                .discountPrice(createBundle.getDiscountPrice())
                .coverImage("https://s3.example.com/image.jpg")
                .status(2)
                .createdDate(LocalDateTime.now())
                .build();

        when(bundleRepository.save(any(Bundle.class))).thenReturn(savedBundle);

        // When
        BundleDto result = bundleService.create(createBundle);

        // Then
        assertNotNull(result);
        assertEquals(createBundle.getName(), result.getName());
        assertEquals(createBundle.getDescription(), result.getDescription());
        verify(bundleRepository).findByName(createBundle.getName());
        verify(bundleRepository).save(any(Bundle.class));
        verify(s3Service).uploadBytes(any(byte[].class), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw ConflictException when bundle name already exists")
    void testCreateBundle_NameExists() {
        // Given
        when(bundleRepository.findByName(createBundle.getName())).thenReturn(Optional.of(bundle));

        // When & Then
        assertThrows(ConflictException.class, () -> {
            bundleService.create(createBundle);
        });
        verify(bundleRepository).findByName(createBundle.getName());
        verify(bundleRepository, never()).save(any(Bundle.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException when discount price is greater than or equal to price")
    void testCreateBundle_InvalidDiscountPrice() {
        // Given
        createBundle.setDiscountPrice(createBundle.getPrice()); // Equal to price
        when(bundleRepository.findByName(createBundle.getName())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BadRequestException.class, () -> {
            bundleService.create(createBundle);
        });
        verify(bundleRepository).findByName(createBundle.getName());
        verify(bundleRepository, never()).save(any(Bundle.class));
    }

    @Test
    @DisplayName("Should update bundle successfully")
    void testUpdateBundle_Success() {
        // Given
        when(bundleRepository.findNoneDeleteById(bundleId)).thenReturn(Optional.of(bundle));
        when(bundleRepository.findByName(updateBundle.getName())).thenReturn(Optional.empty());

        Bundle updatedBundle = Bundle.builder()
                .id(bundleId)
                .name(updateBundle.getName())
                .description(updateBundle.getDescription())
                .price(updateBundle.getPrice())
                .discountPrice(updateBundle.getDiscountPrice())
                .coverImage(updateBundle.getCoverImage())
                .status(updateBundle.getStatus())
                .createdDate(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();

        when(bundleRepository.save(any(Bundle.class))).thenReturn(updatedBundle);

        // When
        BundleDto result = bundleService.update(bundleId, updateBundle);

        // Then
        assertNotNull(result);
        assertEquals(updateBundle.getName(), result.getName());
        verify(bundleRepository).findNoneDeleteById(bundleId);
        verify(bundleRepository).save(any(Bundle.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException when updating non-existent bundle")
    void testUpdateBundle_NotFound() {
        // Given
        when(bundleRepository.findNoneDeleteById(bundleId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BadRequestException.class, () -> {
            bundleService.update(bundleId, updateBundle);
        });
        verify(bundleRepository).findNoneDeleteById(bundleId);
        verify(bundleRepository, never()).save(any(Bundle.class));
    }

    @Test
    @DisplayName("Should throw ConflictException when bundle name already exists during update")
    void testUpdateBundle_NameExists() {
        // Given
        Bundle existingBundleWithName = Bundle.builder()
                .id(UUID.randomUUID())
                .name(updateBundle.getName())
                .build();

        when(bundleRepository.findNoneDeleteById(bundleId)).thenReturn(Optional.of(bundle));
        when(bundleRepository.findByName(updateBundle.getName())).thenReturn(Optional.of(existingBundleWithName));

        // When & Then
        assertThrows(ConflictException.class, () -> {
            bundleService.update(bundleId, updateBundle);
        });
        verify(bundleRepository).findNoneDeleteById(bundleId);
        verify(bundleRepository).findByName(updateBundle.getName());
        verify(bundleRepository, never()).save(any(Bundle.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException when discount price is invalid during update")
    void testUpdateBundle_InvalidDiscountPrice() {
        // Given
        updateBundle.setDiscountPrice(updateBundle.getPrice()); // Equal to price
        when(bundleRepository.findNoneDeleteById(bundleId)).thenReturn(Optional.of(bundle));
        when(bundleRepository.findByName(updateBundle.getName())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BadRequestException.class, () -> {
            bundleService.update(bundleId, updateBundle);
        });
        verify(bundleRepository).findNoneDeleteById(bundleId);
        verify(bundleRepository, never()).save(any(Bundle.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException when updating without image or imageUrl")
    void testUpdateBundle_NoImage() {
        // Given
        updateBundle.setCoverImage(null);
        updateBundle.setImage(null);
        when(bundleRepository.findNoneDeleteById(bundleId)).thenReturn(Optional.of(bundle));
        when(bundleRepository.findByName(updateBundle.getName())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BadRequestException.class, () -> {
            bundleService.update(bundleId, updateBundle);
        });
        verify(bundleRepository).findNoneDeleteById(bundleId);
        verify(bundleRepository, never()).save(any(Bundle.class));
    }

    @Test
    @DisplayName("Should patch update bundle successfully")
    void testPatchBundle_Success() {
        // Given
        when(bundleRepository.findNoneDeleteById(bundleId)).thenReturn(Optional.of(bundle));
        when(bundleRepository.findByName(patchBundle.getName())).thenReturn(Optional.empty());

        Bundle patchedBundle = Bundle.builder()
                .id(bundleId)
                .name(patchBundle.getName())
                .description(bundle.getDescription())
                .price(patchBundle.getPrice())
                .discountPrice(patchBundle.getDiscountPrice())
                .coverImage(bundle.getCoverImage())
                .status(bundle.getStatus())
                .createdDate(bundle.getCreatedDate())
                .lastUpdated(LocalDateTime.now())
                .build();

        when(bundleRepository.save(any(Bundle.class))).thenReturn(patchedBundle);

        // When
        BundleDto result = bundleService.patch(bundleId, patchBundle);

        // Then
        assertNotNull(result);
        assertEquals(patchBundle.getName(), result.getName());
        assertEquals(patchBundle.getPrice(), result.getPrice());
        verify(bundleRepository).findNoneDeleteById(bundleId);
        verify(bundleRepository).save(any(Bundle.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException when patching non-existent bundle")
    void testPatchBundle_NotFound() {
        // Given
        when(bundleRepository.findNoneDeleteById(bundleId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BadRequestException.class, () -> {
            bundleService.patch(bundleId, patchBundle);
        });
        verify(bundleRepository).findNoneDeleteById(bundleId);
        verify(bundleRepository, never()).save(any(Bundle.class));
    }

    @Test
    @DisplayName("Should delete bundle successfully")
    void testDeleteBundle_Success() {
        // Given
        when(bundleRepository.findNoneDeleteById(bundleId)).thenReturn(Optional.of(bundle));

        Bundle deletedBundle = Bundle.builder()
                .id(bundleId)
                .status(0)
                .lastUpdated(LocalDateTime.now())
                .build();

        when(bundleRepository.save(any(Bundle.class))).thenReturn(deletedBundle);

        // When
        assertDoesNotThrow(() -> {
            bundleService.delete(bundleId);
        });

        // Then
        verify(bundleRepository).findNoneDeleteById(bundleId);
        verify(bundleRepository).save(any(Bundle.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException when deleting non-existent bundle")
    void testDeleteBundle_NotFound() {
        // Given
        when(bundleRepository.findNoneDeleteById(bundleId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BadRequestException.class, () -> {
            bundleService.delete(bundleId);
        });
        verify(bundleRepository).findNoneDeleteById(bundleId);
        verify(bundleRepository, never()).save(any(Bundle.class));
    }

    @Test
    @DisplayName("Should get active bundle with courses successfully")
    void testGetActiveBundleWithCourses_Success() {
        // Given
        Course course = Course.builder().id(UUID.randomUUID()).build();
        CourseBundle courseBundle = new CourseBundle();
        courseBundle.setCourse(course);
        bundle.setCourseBundles(List.of(courseBundle));

        when(bundleRepository.findActiveBundleWithCourses(bundleId)).thenReturn(Optional.of(bundle));

        // When
        BundleDto result = bundleService.getActiveBundleWithCourses(bundleId);

        // Then
        assertNotNull(result);
        assertEquals(bundleId, result.getId());
        verify(bundleRepository).findActiveBundleWithCourses(bundleId);
    }

    @Test
    @DisplayName("Should throw BadRequestException when active bundle with courses not found")
    void testGetActiveBundleWithCourses_NotFound() {
        // Given
        when(bundleRepository.findActiveBundleWithCourses(bundleId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BadRequestException.class, () -> {
            bundleService.getActiveBundleWithCourses(bundleId);
        });
        verify(bundleRepository).findActiveBundleWithCourses(bundleId);
    }
}

