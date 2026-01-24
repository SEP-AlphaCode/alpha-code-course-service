package site.alphacode.alphacodecourseservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import site.alphacode.alphacodecourseservice.dto.request.ReorderSectionsRequest;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateSection;
import site.alphacode.alphacodecourseservice.dto.request.update.UpdateSection;
import site.alphacode.alphacodecourseservice.dto.response.SectionDto;
import site.alphacode.alphacodecourseservice.dto.response.SectionWithAccountLesson;
import site.alphacode.alphacodecourseservice.entity.Course;
import site.alphacode.alphacodecourseservice.entity.Lesson;
import site.alphacode.alphacodecourseservice.entity.Section;
import site.alphacode.alphacodecourseservice.exception.ConflictException;
import site.alphacode.alphacodecourseservice.exception.ResourceNotFoundException;
import site.alphacode.alphacodecourseservice.repository.*;
import site.alphacode.alphacodecourseservice.service.implement.SectionServiceImplement;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SectionService Unit Tests")
class SectionServiceTest {

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private AccountLessonRepository accountLessonRepository;

    @Mock
    private SubmissionRepository submissionRepository;

    @InjectMocks
    private SectionServiceImplement sectionService;

    private UUID sectionId;
    private UUID courseId;
    private UUID accountId;
    private Section section;
    private Course course;
    private CreateSection createSection;
    private UpdateSection updateSection;

    @BeforeEach
    void setUp() {
        sectionId = UUID.randomUUID();
        courseId = UUID.randomUUID();
        accountId = UUID.randomUUID();

        course = Course.builder()
                .id(courseId)
                .name("Test Course")
                .slug("test-course")
                .status(1)
                .createdDate(LocalDateTime.now())
                .build();

        section = Section.builder()
                .id(sectionId)
                .title("Test Section")
                .orderNumber(1)
                .courseId(courseId)
                .course(course)
                .status(1)
                .createdDate(LocalDateTime.now())
                .build();

        createSection = new CreateSection();
        createSection.setTitle("New Section");
        createSection.setCourseId(courseId);

        updateSection = new UpdateSection();
        updateSection.setTitle("Updated Section");
        updateSection.setOrderNumber(1);
    }

    @Test
    @DisplayName("Should get section by id successfully")
    void testGetById_Success() {
        // Given
        when(sectionRepository.findById(sectionId)).thenReturn(Optional.of(section));
        when(lessonRepository.findAllNoneDeletedBySectionIdOrderByOrderNumberAsc(sectionId)).thenReturn(List.of());

        // When
        SectionDto result = sectionService.getById(sectionId);

        // Then
        assertNotNull(result);
        assertEquals(sectionId, result.getId());
        assertEquals("Test Section", result.getTitle());
        verify(sectionRepository).findById(sectionId);
        verify(lessonRepository).findAllNoneDeletedBySectionIdOrderByOrderNumberAsc(sectionId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when section not found")
    void testGetById_NotFound() {
        // Given
        when(sectionRepository.findById(sectionId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            sectionService.getById(sectionId);
        });
        verify(sectionRepository).findById(sectionId);
    }

    @Test
    @DisplayName("Should get all sections by course id successfully")
    void testGetAllByCourseId_Success() {
        // Given
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(sectionRepository.findAllByCourseId(courseId)).thenReturn(List.of(section));
        when(lessonRepository.findAllNoneDeletedBySectionIdOrderByOrderNumberAsc(sectionId)).thenReturn(List.of());

        // When
        List<SectionDto> result = sectionService.getAllByCourseId(courseId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(courseRepository).findById(courseId);
        verify(sectionRepository).findAllByCourseId(courseId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when course not found")
    void testGetAllByCourseId_CourseNotFound() {
        // Given
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            sectionService.getAllByCourseId(courseId);
        });
        verify(courseRepository).findById(courseId);
        verify(sectionRepository, never()).findAllByCourseId(any());
    }

    @Test
    @DisplayName("Should create section successfully")
    void testCreate_Success() {
        // Given
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(sectionRepository.findByTitleAndCourseId(createSection.getTitle(), courseId)).thenReturn(Optional.empty());
        when(sectionRepository.findMaxOrderNumberByCourseId(courseId)).thenReturn(null);

        Section savedSection = Section.builder()
                .id(sectionId)
                .title(createSection.getTitle())
                .orderNumber(1)
                .courseId(courseId)
                .status(1)
                .createdDate(LocalDateTime.now())
                .build();

        when(sectionRepository.save(any(Section.class))).thenReturn(savedSection);
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        // When
        SectionDto result = sectionService.create(createSection);

        // Then
        assertNotNull(result);
        assertEquals(createSection.getTitle(), result.getTitle());
        verify(courseRepository).findById(courseId);
        verify(sectionRepository).findByTitleAndCourseId(createSection.getTitle(), courseId);
        verify(sectionRepository).save(any(Section.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when course not found during create")
    void testCreate_CourseNotFound() {
        // Given
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            sectionService.create(createSection);
        });
        verify(courseRepository).findById(courseId);
        verify(sectionRepository, never()).save(any(Section.class));
    }

    @Test
    @DisplayName("Should throw ConflictException when section title already exists")
    void testCreate_TitleExists() {
        // Given
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(sectionRepository.findByTitleAndCourseId(createSection.getTitle(), courseId)).thenReturn(Optional.of(section));

        // When & Then
        assertThrows(ConflictException.class, () -> {
            sectionService.create(createSection);
        });
        verify(courseRepository).findById(courseId);
        verify(sectionRepository).findByTitleAndCourseId(createSection.getTitle(), courseId);
        verify(sectionRepository, never()).save(any(Section.class));
    }

    @Test
    @DisplayName("Should update section successfully")
    void testUpdate_Success() {
        // Given
        when(sectionRepository.findById(sectionId)).thenReturn(Optional.of(section));
        when(sectionRepository.findByTitleAndCourseId(updateSection.getTitle(), courseId)).thenReturn(Optional.empty());
        when(lessonRepository.findAllNoneDeletedBySectionIdOrderByOrderNumberAsc(sectionId)).thenReturn(List.of());

        Section updatedSection = Section.builder()
                .id(sectionId)
                .title(updateSection.getTitle())
                .orderNumber(updateSection.getOrderNumber())
                .courseId(courseId)
                .lastUpdated(LocalDateTime.now())
                .build();

        when(sectionRepository.save(any(Section.class))).thenReturn(updatedSection);

        // When
        SectionDto result = sectionService.update(sectionId, updateSection);

        // Then
        assertNotNull(result);
        assertEquals(updateSection.getTitle(), result.getTitle());
        verify(sectionRepository).findById(sectionId);
        verify(sectionRepository).save(any(Section.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent section")
    void testUpdate_NotFound() {
        // Given
        when(sectionRepository.findById(sectionId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            sectionService.update(sectionId, updateSection);
        });
        verify(sectionRepository).findById(sectionId);
        verify(sectionRepository, never()).save(any(Section.class));
    }

    @Test
    @DisplayName("Should throw ConflictException when section title already exists during update")
    void testUpdate_TitleExists() {
        // Given
        Section existingSectionWithTitle = Section.builder()
                .id(UUID.randomUUID())
                .title(updateSection.getTitle())
                .courseId(courseId)
                .build();

        when(sectionRepository.findById(sectionId)).thenReturn(Optional.of(section));
        when(sectionRepository.findByTitleAndCourseId(updateSection.getTitle(), courseId)).thenReturn(Optional.of(existingSectionWithTitle));

        // When & Then
        assertThrows(ConflictException.class, () -> {
            sectionService.update(sectionId, updateSection);
        });
        verify(sectionRepository).findById(sectionId);
        verify(sectionRepository, never()).save(any(Section.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent section")
    void testDelete_NotFound() {
        // Given
        when(sectionRepository.findById(sectionId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            sectionService.delete(sectionId);
        });
        verify(sectionRepository).findById(sectionId);
        verify(sectionRepository, never()).save(any(Section.class));
    }

    @Test
    @DisplayName("Should get all sections with account lesson successfully")
    void testGetAllSectionWithAccountLesson_Success() {
        // Given
        when(courseRepository.findNoneDeleteCourseById(courseId)).thenReturn(Optional.of(course));
        when(sectionRepository.findAllByCourseId(courseId)).thenReturn(List.of(section));
        when(lessonRepository.findAllBySectionIdOrderByOrderNumberAsc(sectionId)).thenReturn(List.of());
        when(accountLessonRepository.findAllByAccountIdAndCourseId(accountId, courseId)).thenReturn(List.of());

        // When
        List<SectionWithAccountLesson> result = sectionService.getAllSectionWithAccountLesson(courseId, accountId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(courseRepository).findNoneDeleteCourseById(courseId);
        verify(sectionRepository).findAllByCourseId(courseId);
        verify(accountLessonRepository).findAllByAccountIdAndCourseId(accountId, courseId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when course not found in getAllSectionWithAccountLesson")
    void testGetAllSectionWithAccountLesson_CourseNotFound() {
        // Given
        when(courseRepository.findNoneDeleteCourseById(courseId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            sectionService.getAllSectionWithAccountLesson(courseId, accountId);
        });
        verify(courseRepository).findNoneDeleteCourseById(courseId);
        verify(sectionRepository, never()).findAllByCourseId(any());
    }

    @Test
    @DisplayName("Should get all sections with account lesson by slug successfully")
    void testGetAllSectionWithAccountLessonBySlug_Success() {
        // Given
        String slug = "test-course";
        when(courseRepository.findCourseBySlug(slug)).thenReturn(Optional.of(course));
        when(sectionRepository.findAllByCourseId(courseId)).thenReturn(List.of(section));
        when(lessonRepository.findAllBySectionIdOrderByOrderNumberAsc(sectionId)).thenReturn(List.of());
        when(accountLessonRepository.findAllByAccountIdAndCourseId(accountId, courseId)).thenReturn(List.of());

        // When
        List<SectionWithAccountLesson> result = sectionService.getAllSectionWithAccountLessonBySlug(slug, accountId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(courseRepository).findCourseBySlug(slug);
        verify(sectionRepository).findAllByCourseId(courseId);
        verify(accountLessonRepository).findAllByAccountIdAndCourseId(accountId, courseId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when course by slug not found")
    void testGetAllSectionWithAccountLessonBySlug_CourseNotFound() {
        // Given
        String slug = "non-existent-slug";
        when(courseRepository.findCourseBySlug(slug)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            sectionService.getAllSectionWithAccountLessonBySlug(slug, accountId);
        });
        verify(courseRepository).findCourseBySlug(slug);
        verify(sectionRepository, never()).findAllByCourseId(any());
    }
}

