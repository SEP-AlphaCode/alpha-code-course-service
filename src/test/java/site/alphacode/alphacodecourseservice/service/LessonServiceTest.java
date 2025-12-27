package site.alphacode.alphacodecourseservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateLesson;
import site.alphacode.alphacodecourseservice.dto.response.LessonDto;
import site.alphacode.alphacodecourseservice.dto.response.LessonWithSolution;
import site.alphacode.alphacodecourseservice.dto.response.PagedResult;
import site.alphacode.alphacodecourseservice.entity.Course;
import site.alphacode.alphacodecourseservice.entity.Lesson;
import site.alphacode.alphacodecourseservice.entity.Section;
import site.alphacode.alphacodecourseservice.exception.ResourceNotFoundException;
import site.alphacode.alphacodecourseservice.repository.AccountLessonRepository;
import site.alphacode.alphacodecourseservice.repository.CourseRepository;
import site.alphacode.alphacodecourseservice.repository.LessonRepository;
import site.alphacode.alphacodecourseservice.repository.SectionRepository;
import site.alphacode.alphacodecourseservice.service.implement.LessonServiceImplement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LessonService Unit Tests")
class LessonServiceTest {

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private AccountLessonRepository accountLessonRepository;

    @InjectMocks
    private LessonServiceImplement lessonService;

    private UUID lessonId;
    private UUID sectionId;
    private UUID courseId;
    private Lesson lesson;
    private Section section;
    private Course course;
    private CreateLesson createLesson;

    @BeforeEach
    void setUp() {
        lessonId = UUID.randomUUID();
        sectionId = UUID.randomUUID();
        courseId = UUID.randomUUID();

        course = Course.builder()
                .id(courseId)
                .name("Test Course")
                .status(1)
                .build();

        section = Section.builder()
                .id(sectionId)
                .title("Test Section")
                .courseId(courseId)
                .course(course)
                .orderNumber(1)
                .status(1)
                .createdDate(LocalDateTime.now())
                .build();

        lesson = Lesson.builder()
                .id(lessonId)
                .title("Test Lesson")
                .slug("test-lesson")
                .content("Test Content")
                .videoUrl("https://example.com/video.mp4")
                .duration(3600)
                .requireRobot(false)
                .orderNumber(1)
                .type(1)
                .sectionId(sectionId)
                .section(section)
                .status(1)
                .createdDate(LocalDateTime.now())
                .build();

        createLesson = CreateLesson.builder()
                .title("New Lesson")
                .content("New Content")
                .videoUrl("https://example.com/new-video.mp4")
                .duration(1800)
                .requireRobot(true)
                .type(1)
                .sectionId(sectionId)
                .build();
    }

    @Test
    @DisplayName("Should get lesson by id successfully")
    void testGetLessonById_Success() {
        // Given
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));

        // When
        LessonDto result = lessonService.getLessonById(lessonId);

        // Then
        assertNotNull(result);
        assertEquals(lessonId, result.getId());
        assertEquals("Test Lesson", result.getTitle());
        verify(lessonRepository).findById(lessonId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when lesson not found")
    void testGetLessonById_NotFound() {
        // Given
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            lessonService.getLessonById(lessonId);
        });
        verify(lessonRepository).findById(lessonId);
    }

    @Test
    @DisplayName("Should get lesson with solution by id successfully")
    void testGetLessonWithSolutionById_Success() {
        // Given
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));

        // When
        LessonWithSolution result = lessonService.getLessonWithSolutionById(lessonId);

        // Then
        assertNotNull(result);
        assertEquals(lessonId, result.getId());
        verify(lessonRepository).findById(lessonId);
    }

    @Test
    @DisplayName("Should get lesson by slug successfully")
    void testGetLessonBySlug_Success() {
        // Given
        String slug = "test-lesson";
        when(lessonRepository.findBySlug(slug)).thenReturn(Optional.of(lesson));

        // When
        LessonDto result = lessonService.getLessonBySlug(slug);

        // Then
        assertNotNull(result);
        assertEquals(slug, result.getSlug());
        verify(lessonRepository).findBySlug(slug);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when lesson by slug not found")
    void testGetLessonBySlug_NotFound() {
        // Given
        String slug = "non-existent-slug";
        when(lessonRepository.findBySlug(slug)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            lessonService.getLessonBySlug(slug);
        });
        verify(lessonRepository).findBySlug(slug);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when section not found")
    void testGetActiveLessonsBySectionId_SectionNotFound() {
        // Given
        int page = 1;
        int size = 10;
        when(sectionRepository.findById(sectionId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            lessonService.getActiveLessonsBySectionId(sectionId, page, size);
        });
        verify(sectionRepository).findById(sectionId);
        verify(lessonRepository, never()).findAllActiveLessonsBySectionId(any(), any());
    }

    @Test
    @DisplayName("Should get lesson with solution by slug successfully")
    void testGetLessonWithSolutionBySlug_Success() {
        // Given
        String slug = "test-lesson";
        when(lessonRepository.findBySlug(slug)).thenReturn(Optional.of(lesson));

        // When
        LessonWithSolution result = lessonService.getLessonWithSolutionBySlug(slug);

        // Then
        assertNotNull(result);
        assertEquals(slug, result.getSlug());
        verify(lessonRepository).findBySlug(slug);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when lesson with solution by slug not found")
    void testGetLessonWithSolutionBySlug_NotFound() {
        // Given
        String slug = "non-existent-slug";
        when(lessonRepository.findBySlug(slug)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            lessonService.getLessonWithSolutionBySlug(slug);
        });
        verify(lessonRepository).findBySlug(slug);
    }
}

