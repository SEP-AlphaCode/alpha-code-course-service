package site.alphacode.alphacodecourseservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateCourse;
import site.alphacode.alphacodecourseservice.dto.request.patch.PatchCourse;
import site.alphacode.alphacodecourseservice.dto.request.update.UpdateCourse;
import site.alphacode.alphacodecourseservice.dto.response.CourseDto;
import site.alphacode.alphacodecourseservice.dto.response.PagedResult;
import site.alphacode.alphacodecourseservice.entity.Category;
import site.alphacode.alphacodecourseservice.entity.Course;
import site.alphacode.alphacodecourseservice.exception.BadRequestException;
import site.alphacode.alphacodecourseservice.exception.ConflictException;
import site.alphacode.alphacodecourseservice.exception.ResourceNotFoundException;
import site.alphacode.alphacodecourseservice.repository.*;
import site.alphacode.alphacodecourseservice.service.S3Service;
import site.alphacode.alphacodecourseservice.service.implement.CourseServiceImplement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CourseService Unit Tests")
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private AccountLessonRepository accountLessonRepository;

    @Mock
    private AccountCourseRepository accountCourseRepository;

    @Mock
    private Cache courseCache;

    @InjectMocks
    private CourseServiceImplement courseService;

    private UUID courseId;
    private UUID categoryId;
    private Course course;
    private Category category;
    private CreateCourse createCourse;
    private UpdateCourse updateCourse;
    private PatchCourse patchCourse;
    private MultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        courseId = UUID.randomUUID();
        categoryId = UUID.randomUUID();

        category = Category.builder()
                .id(categoryId)
                .name("Test Category")
                .description("Test Description")
                .slug("test-category")
                .status(1)
                .createdDate(LocalDateTime.now())
                .build();

        course = Course.builder()
                .id(courseId)
                .name("Test Course")
                .description("Test Description")
                .price(100000)
                .requireLicense(false)
                .level(1)
                .totalLessons(5)
                .totalDuration(3600)
                .slug("test-course")
                .categoryId(categoryId)
                .status(1)
                .createdDate(LocalDateTime.now())
                .build();

        multipartFile = mock(MultipartFile.class);

        createCourse = CreateCourse.builder()
                .name("New Course")
                .description("New Description")
                .price(200000)
                .requireLicense(true)
                .level(2)
                .categoryId(categoryId)
                .image(multipartFile)
                .build();

        updateCourse = new UpdateCourse();
        updateCourse.setName("Updated Course");
        updateCourse.setDescription("Updated Description");
        updateCourse.setPrice(300000);
        updateCourse.setRequireLicense(false);
        updateCourse.setLevel(3);
        updateCourse.setCategoryId(categoryId);
        updateCourse.setStatus(1);
        updateCourse.setImageUrl("https://example.com/image.jpg");

        patchCourse = new PatchCourse();
        patchCourse.setName("Patched Course");
        patchCourse.setPrice(250000);
    }

    @Test
    @DisplayName("Should get active course by id successfully")
    void testGetActiveCourseById_Success() {
        // Given
        when(courseRepository.findActiveCourseById(courseId)).thenReturn(Optional.of(course));

        // When
        CourseDto result = courseService.getActiveCourseById(courseId);

        // Then
        assertNotNull(result);
        assertEquals(courseId, result.getId());
        assertEquals("Test Course", result.getName());
        verify(courseRepository).findActiveCourseById(courseId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when course not found")
    void testGetActiveCourseById_NotFound() {
        // Given
        when(courseRepository.findActiveCourseById(courseId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            courseService.getActiveCourseById(courseId);
        });
        verify(courseRepository).findActiveCourseById(courseId);
    }

    @Test
    @DisplayName("Should get course by slug successfully")
    void testGetCourseBySlug_Success() {
        // Given
        String slug = "test-course";
        when(courseRepository.findCourseBySlug(slug)).thenReturn(Optional.of(course));

        // When
        CourseDto result = courseService.getCourseBySlug(slug);

        // Then
        assertNotNull(result);
        assertEquals(slug, result.getSlug());
        verify(courseRepository).findCourseBySlug(slug);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when course by slug not found")
    void testGetCourseBySlug_NotFound() {
        // Given
        String slug = "non-existent-slug";
        when(courseRepository.findCourseBySlug(slug)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            courseService.getCourseBySlug(slug);
        });
        verify(courseRepository).findCourseBySlug(slug);
    }

    @Test
    @DisplayName("Should get all active courses with pagination")
    void testGetAllActiveCourses_Success() {
        // Given
        int page = 1;
        int size = 10;
        String search = "test";
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Course> coursePage = new PageImpl<>(List.of(course), pageable, 1);

        when(courseRepository.findAllActiveCourse(search, categoryId, pageable)).thenReturn(coursePage);
        when(sectionRepository.countActiveByCourseId(courseId)).thenReturn(3L);

        // When
        PagedResult<CourseDto> result = courseService.getAllActiveCourses(page, size, search, categoryId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getData().size());
        verify(courseRepository).findAllActiveCourse(search, categoryId, pageable);
        verify(sectionRepository).countActiveByCourseId(courseId);
    }

    @Test
    @DisplayName("Should create course successfully")
    void testCreateCourse_Success() throws Exception {
        // Given
        when(courseRepository.existsByName(createCourse.getName())).thenReturn(false);
        when(categoryRepository.findNoneDeleteCategoryById(categoryId)).thenReturn(Optional.of(category));
        when(multipartFile.getBytes()).thenReturn("image bytes".getBytes());
        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.isEmpty()).thenReturn(false);
        when(s3Service.uploadBytes(any(byte[].class), anyString(), anyString())).thenReturn("https://s3.example.com/image.jpg");

        Course savedCourse = Course.builder()
                .id(courseId)
                .name(createCourse.getName())
                .description(createCourse.getDescription())
                .price(createCourse.getPrice())
                .requireLicense(createCourse.getRequireLicense())
                .level(createCourse.getLevel())
                .categoryId(createCourse.getCategoryId())
                .slug("new-course")
                .status(2)
                .totalLessons(0)
                .totalDuration(0)
                .imageUrl("https://s3.example.com/image.jpg")
                .createdDate(LocalDateTime.now())
                .build();

        when(courseRepository.save(any(Course.class))).thenReturn(savedCourse);

        // When
        CourseDto result = courseService.create(createCourse);

        // Then
        assertNotNull(result);
        assertEquals(createCourse.getName(), result.getName());
        assertEquals(createCourse.getDescription(), result.getDescription());
        verify(courseRepository).existsByName(createCourse.getName());
        verify(categoryRepository).findNoneDeleteCategoryById(categoryId);
        verify(courseRepository).save(any(Course.class));
        verify(s3Service).uploadBytes(any(byte[].class), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when course name already exists")
    void testCreateCourse_NameExists() {
        // Given
        when(courseRepository.existsByName(createCourse.getName())).thenReturn(true);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            courseService.create(createCourse);
        });
        verify(courseRepository).existsByName(createCourse.getName());
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when category not found")
    void testCreateCourse_CategoryNotFound() {
        // Given
        when(courseRepository.existsByName(createCourse.getName())).thenReturn(false);
        when(categoryRepository.findNoneDeleteCategoryById(categoryId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            courseService.create(createCourse);
        });
        verify(courseRepository).existsByName(createCourse.getName());
        verify(categoryRepository).findNoneDeleteCategoryById(categoryId);
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    @DisplayName("Should update course successfully")
    void testUpdateCourse_Success() {
        // Given
        when(courseRepository.findNoneDeleteCourseById(courseId)).thenReturn(Optional.of(course));
        when(courseRepository.existsByName(updateCourse.getName())).thenReturn(false);
        when(categoryRepository.findNoneDeleteCategoryById(categoryId)).thenReturn(Optional.of(category));

        Course updatedCourse = Course.builder()
                .id(courseId)
                .name(updateCourse.getName())
                .description(updateCourse.getDescription())
                .price(updateCourse.getPrice())
                .requireLicense(updateCourse.getRequireLicense())
                .level(updateCourse.getLevel())
                .categoryId(updateCourse.getCategoryId())
                .slug("updated-course")
                .status(updateCourse.getStatus())
                .totalLessons(5)
                .totalDuration(3600)
                .imageUrl(updateCourse.getImageUrl())
                .createdDate(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();

        when(courseRepository.save(any(Course.class))).thenReturn(updatedCourse);

        // When
        CourseDto result = courseService.update(courseId, updateCourse);

        // Then
        assertNotNull(result);
        assertEquals(updateCourse.getName(), result.getName());
        verify(courseRepository).findNoneDeleteCourseById(courseId);
        verify(courseRepository).existsByName(updateCourse.getName());
        verify(categoryRepository).findNoneDeleteCategoryById(categoryId);
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent course")
    void testUpdateCourse_NotFound() {
        // Given
        when(courseRepository.findNoneDeleteCourseById(courseId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            courseService.update(courseId, updateCourse);
        });
        verify(courseRepository).findNoneDeleteCourseById(courseId);
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    @DisplayName("Should throw ConflictException when course name already exists during update")
    void testUpdateCourse_NameExists() {
        // Given
        when(courseRepository.findNoneDeleteCourseById(courseId)).thenReturn(Optional.of(course));
        when(courseRepository.existsByName(updateCourse.getName())).thenReturn(true);

        // When & Then
        assertThrows(ConflictException.class, () -> {
            courseService.update(courseId, updateCourse);
        });
        verify(courseRepository).findNoneDeleteCourseById(courseId);
        verify(courseRepository).existsByName(updateCourse.getName());
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException when trying to activate course with no lessons")
    void testUpdateCourse_ActivateWithoutLessons() {
        // Given
        Course courseWithoutLessons = Course.builder()
                .id(courseId)
                .name("Course")
                .totalLessons(0)
                .build();

        updateCourse.setStatus(1); // Active status

        when(courseRepository.findNoneDeleteCourseById(courseId)).thenReturn(Optional.of(courseWithoutLessons));
        when(courseRepository.existsByName(updateCourse.getName())).thenReturn(false);
        when(categoryRepository.findNoneDeleteCategoryById(categoryId)).thenReturn(Optional.of(category));

        // When & Then
        assertThrows(BadRequestException.class, () -> {
            courseService.update(courseId, updateCourse);
        });
        verify(courseRepository).findNoneDeleteCourseById(courseId);
    }

    @Test
    @DisplayName("Should patch update course successfully")
    void testPatchUpdateCourse_Success() {
        // Given
        when(courseRepository.findNoneDeleteCourseById(courseId)).thenReturn(Optional.of(course));
        when(courseRepository.existsByName(patchCourse.getName())).thenReturn(false);

        Course patchedCourse = Course.builder()
                .id(courseId)
                .name(patchCourse.getName())
                .description(course.getDescription())
                .price(patchCourse.getPrice())
                .requireLicense(course.getRequireLicense())
                .level(course.getLevel())
                .categoryId(course.getCategoryId())
                .slug("patched-course")
                .status(course.getStatus())
                .totalLessons(course.getTotalLessons())
                .totalDuration(course.getTotalDuration())
                .createdDate(course.getCreatedDate())
                .lastUpdated(LocalDateTime.now())
                .build();

        when(courseRepository.save(any(Course.class))).thenReturn(patchedCourse);

        // When
        CourseDto result = courseService.patchUpdate(courseId, patchCourse);

        // Then
        assertNotNull(result);
        assertEquals(patchCourse.getName(), result.getName());
        assertEquals(patchCourse.getPrice(), result.getPrice());
        verify(courseRepository).findNoneDeleteCourseById(courseId);
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    @DisplayName("Should delete course successfully")
    void testDeleteCourse_Success() {
        // Given
        when(courseRepository.findNoneDeleteCourseById(courseId)).thenReturn(Optional.of(course));
        when(sectionRepository.findAllByCourseId(courseId)).thenReturn(List.of());
        when(accountLessonRepository.findAllByCourseId(courseId)).thenReturn(List.of());
        when(accountCourseRepository.findAllByCourseId(courseId)).thenReturn(List.of());

        Course deletedCourse = Course.builder()
                .id(courseId)
                .status(0)
                .totalLessons(0)
                .totalDuration(0)
                .lastUpdated(LocalDateTime.now())
                .build();

        when(courseRepository.save(any(Course.class))).thenReturn(deletedCourse);

        // When
        assertDoesNotThrow(() -> {
            courseService.delete(courseId);
        });

        // Then
        verify(courseRepository).findNoneDeleteCourseById(courseId);
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent course")
    void testDeleteCourse_NotFound() {
        // Given
        when(courseRepository.findNoneDeleteCourseById(courseId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            courseService.delete(courseId);
        });
        verify(courseRepository).findNoneDeleteCourseById(courseId);
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    @DisplayName("Should get courses by ids successfully")
    void testGetCoursesByIds_Success() {
        // Given
        List<UUID> courseIds = List.of(courseId, UUID.randomUUID());
        List<Course> courses = List.of(course);
        when(courseRepository.findAllByIds(courseIds)).thenReturn(courses);

        // When
        List<Course> result = courseService.getCoursesByIds(courseIds);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(courseRepository).findAllByIds(courseIds);
    }

    @Test
    @DisplayName("Should return empty list when course ids list is null")
    void testGetCoursesByIds_NullList() {
        // When
        List<Course> result = courseService.getCoursesByIds(null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(courseRepository, never()).findAllByIds(any());
    }

    @Test
    @DisplayName("Should return empty list when course ids list is empty")
    void testGetCoursesByIds_EmptyList() {
        // When
        List<Course> result = courseService.getCoursesByIds(List.of());

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(courseRepository, never()).findAllByIds(any());
    }

    @Test
    @DisplayName("Should get none delete course by id successfully")
    void testGetNoneDeleteCourseById_Success() {
        // Given
        when(courseRepository.findNoneDeleteCourseById(courseId)).thenReturn(Optional.of(course));

        // When
        CourseDto result = courseService.getNoneDeleteCourseById(courseId);

        // Then
        assertNotNull(result);
        assertEquals(courseId, result.getId());
        verify(courseRepository).findNoneDeleteCourseById(courseId);
    }
}

