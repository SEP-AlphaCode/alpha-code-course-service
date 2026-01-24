package site.alphacode.alphacodecourseservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateCourseBundle;
import site.alphacode.alphacodecourseservice.dto.request.patch.PatchCourseBundle;
import site.alphacode.alphacodecourseservice.dto.request.update.UpdateCourseBundle;
import site.alphacode.alphacodecourseservice.dto.response.CourseBundleDto;
import site.alphacode.alphacodecourseservice.dto.response.CourseDto;
import site.alphacode.alphacodecourseservice.entity.Bundle;
import site.alphacode.alphacodecourseservice.entity.Course;
import site.alphacode.alphacodecourseservice.entity.CourseBundle;
import site.alphacode.alphacodecourseservice.exception.ResourceNotFoundException;
import site.alphacode.alphacodecourseservice.repository.BundleRepository;
import site.alphacode.alphacodecourseservice.repository.CourseBundleRepository;
import site.alphacode.alphacodecourseservice.repository.CourseRepository;
import site.alphacode.alphacodecourseservice.service.implement.CourseBundleServiceImplement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CourseBundleService Unit Tests")
class CourseBundleServiceTest {

    @Mock
    private CourseBundleRepository courseBundleRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private BundleRepository bundleRepository;

    @InjectMocks
    private CourseBundleServiceImplement courseBundleService;

    private UUID courseBundleId;
    private UUID bundleId;
    private UUID courseId;
    private CourseBundle courseBundle;
    private Bundle bundle;
    private Course course;
    private CreateCourseBundle createCourseBundle;
    private UpdateCourseBundle updateCourseBundle;
    private PatchCourseBundle patchCourseBundle;

    @BeforeEach
    void setUp() {
        courseBundleId = UUID.randomUUID();
        bundleId = UUID.randomUUID();
        courseId = UUID.randomUUID();

        bundle = Bundle.builder()
                .id(bundleId)
                .name("Test Bundle")
                .status(1)
                .build();

        course = Course.builder()
                .id(courseId)
                .name("Test Course")
                .status(1)
                .build();

        courseBundle = CourseBundle.builder()
                .id(courseBundleId)
                .courseId(courseId)
                .bundleId(bundleId)
                .status(1)
                .createdDate(LocalDateTime.now())
                .build();

        createCourseBundle = new CreateCourseBundle();
        createCourseBundle.setBundleId(bundleId);
        createCourseBundle.setCourseIds(List.of(courseId));

        updateCourseBundle = new UpdateCourseBundle();
        updateCourseBundle.setId(courseBundleId);
        updateCourseBundle.setCourseId(courseId);
        updateCourseBundle.setBundleId(bundleId);
        updateCourseBundle.setStatus(1);

        patchCourseBundle = new PatchCourseBundle();
        patchCourseBundle.setCourseId(courseId);
        patchCourseBundle.setStatus(1);
    }

    @Test
    @DisplayName("Should get courses by bundle id successfully")
    void testCourseBundle_Success() {
        // Given
        when(bundleRepository.existsById(bundleId)).thenReturn(true);
        when(courseBundleRepository.findCourseIdsByBundleId(bundleId)).thenReturn(List.of(courseId));
        when(courseRepository.findAllById(List.of(courseId))).thenReturn(List.of(course));

        // When
        List<CourseDto> result = courseBundleService.courseBundle(bundleId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bundleRepository).existsById(bundleId);
        verify(courseBundleRepository).findCourseIdsByBundleId(bundleId);
        verify(courseRepository).findAllById(List.of(courseId));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when bundle not found")
    void testCourseBundle_BundleNotFound() {
        // Given
        when(bundleRepository.existsById(bundleId)).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            courseBundleService.courseBundle(bundleId);
        });
        verify(bundleRepository).existsById(bundleId);
        verify(courseBundleRepository, never()).findCourseIdsByBundleId(any());
    }

    @Test
    @DisplayName("Should create course bundle successfully")
    void testCreate_Success() {
        // Given
        when(bundleRepository.findNoneDeleteById(bundleId)).thenReturn(Optional.of(bundle));
        when(courseRepository.findActiveCourseById(courseId)).thenReturn(Optional.of(course));
        when(courseBundleRepository.existsByCourseIdAndBundleId(courseId, bundleId)).thenReturn(false);

        CourseBundle savedCourseBundle = CourseBundle.builder()
                .id(courseBundleId)
                .courseId(courseId)
                .bundleId(bundleId)
                .status(1)
                .createdDate(LocalDateTime.now())
                .build();

        when(courseBundleRepository.save(any(CourseBundle.class))).thenReturn(savedCourseBundle);

        // When
        List<CourseBundleDto> result = courseBundleService.create(createCourseBundle);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bundleRepository).findNoneDeleteById(bundleId);
        verify(courseRepository).findActiveCourseById(courseId);
        verify(courseBundleRepository).existsByCourseIdAndBundleId(courseId, bundleId);
        verify(courseBundleRepository).save(any(CourseBundle.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when bundle not found during create")
    void testCreate_BundleNotFound() {
        // Given
        when(bundleRepository.findNoneDeleteById(bundleId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            courseBundleService.create(createCourseBundle);
        });
        verify(bundleRepository).findNoneDeleteById(bundleId);
        verify(courseBundleRepository, never()).save(any(CourseBundle.class));
    }

    @Test
    @DisplayName("Should update course bundle successfully")
    void testUpdate_Success() {
        // Given
        when(courseBundleRepository.findById(courseBundleId)).thenReturn(Optional.of(courseBundle));
        when(courseRepository.findActiveCourseById(courseId)).thenReturn(Optional.of(course));
        when(bundleRepository.findNoneDeleteById(bundleId)).thenReturn(Optional.of(bundle));

        CourseBundle updatedCourseBundle = CourseBundle.builder()
                .id(courseBundleId)
                .courseId(courseId)
                .bundleId(bundleId)
                .status(updateCourseBundle.getStatus())
                .lastUpdated(LocalDateTime.now())
                .build();

        when(courseBundleRepository.save(any(CourseBundle.class))).thenReturn(updatedCourseBundle);

        // When
        CourseBundleDto result = courseBundleService.update(courseBundleId, updateCourseBundle);

        // Then
        assertNotNull(result);
        verify(courseBundleRepository).findById(courseBundleId);
        verify(courseRepository).findActiveCourseById(courseId);
        verify(bundleRepository).findNoneDeleteById(bundleId);
        verify(courseBundleRepository).save(any(CourseBundle.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent course bundle")
    void testUpdate_NotFound() {
        // Given
        when(courseBundleRepository.findById(courseBundleId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            courseBundleService.update(courseBundleId, updateCourseBundle);
        });
        verify(courseBundleRepository).findById(courseBundleId);
        verify(courseBundleRepository, never()).save(any(CourseBundle.class));
    }

    @Test
    @DisplayName("Should patch course bundle successfully")
    void testPatch_Success() {
        // Given
        when(courseBundleRepository.findById(courseBundleId)).thenReturn(Optional.of(courseBundle));
        when(courseRepository.findActiveCourseById(courseId)).thenReturn(Optional.of(course));

        CourseBundle patchedCourseBundle = CourseBundle.builder()
                .id(courseBundleId)
                .courseId(courseId)
                .bundleId(bundleId)
                .status(patchCourseBundle.getStatus())
                .lastUpdated(LocalDateTime.now())
                .build();

        when(courseBundleRepository.save(any(CourseBundle.class))).thenReturn(patchedCourseBundle);

        // When
        CourseBundleDto result = courseBundleService.patch(courseBundleId, patchCourseBundle);

        // Then
        assertNotNull(result);
        verify(courseBundleRepository).findById(courseBundleId);
        verify(courseBundleRepository).save(any(CourseBundle.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when patching non-existent course bundle")
    void testPatch_NotFound() {
        // Given
        when(courseBundleRepository.findById(courseBundleId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            courseBundleService.patch(courseBundleId, patchCourseBundle);
        });
        verify(courseBundleRepository).findById(courseBundleId);
        verify(courseBundleRepository, never()).save(any(CourseBundle.class));
    }

    @Test
    @DisplayName("Should delete course bundle successfully")
    void testDelete_Success() {
        // Given
        when(courseBundleRepository.findById(courseBundleId)).thenReturn(Optional.of(courseBundle));
        when(courseBundleRepository.save(any(CourseBundle.class))).thenReturn(courseBundle);

        // When
        assertDoesNotThrow(() -> {
            courseBundleService.delete(courseBundleId);
        });

        // Then
        verify(courseBundleRepository).findById(courseBundleId);
        verify(courseBundleRepository).save(any(CourseBundle.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent course bundle")
    void testDelete_NotFound() {
        // Given
        when(courseBundleRepository.findById(courseBundleId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            courseBundleService.delete(courseBundleId);
        });
        verify(courseBundleRepository).findById(courseBundleId);
        verify(courseBundleRepository, never()).save(any(CourseBundle.class));
    }
}

