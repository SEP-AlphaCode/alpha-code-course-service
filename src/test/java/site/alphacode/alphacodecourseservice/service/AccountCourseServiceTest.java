package site.alphacode.alphacodecourseservice.service;

import jakarta.persistence.EntityNotFoundException;
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
import org.springframework.data.redis.core.RedisTemplate;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateAccountCourse;
import site.alphacode.alphacodecourseservice.dto.response.AccountCourseDto;
import site.alphacode.alphacodecourseservice.dto.response.CourseDto;
import site.alphacode.alphacodecourseservice.dto.response.PagedResult;
import site.alphacode.alphacodecourseservice.entity.AccountCourse;
import site.alphacode.alphacodecourseservice.entity.Course;
import site.alphacode.alphacodecourseservice.exception.ConflictException;
import site.alphacode.alphacodecourseservice.repository.*;
import site.alphacode.alphacodecourseservice.service.implement.AccountCourseServiceImplement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountCourseService Unit Tests")
class AccountCourseServiceTest {

    @Mock
    private AccountCourseRepository accountCourseRepository;

    @Mock
    private AccountLessonRepository accountLessonRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private CourseBundleRepository courseBundleRepository;

    @Mock
    private CourseService courseService;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @InjectMocks
    private AccountCourseServiceImplement accountCourseService;

    private UUID accountCourseId;
    private UUID accountId;
    private UUID courseId;
    private AccountCourse accountCourse;
    private Course course;
    private CourseDto courseDto;
    private CreateAccountCourse createAccountCourse;

    @BeforeEach
    void setUp() {
        accountCourseId = UUID.randomUUID();
        accountId = UUID.randomUUID();
        courseId = UUID.randomUUID();

        course = Course.builder()
                .id(courseId)
                .name("Test Course")
                .price(0)
                .status(1)
                .build();

        courseDto = new CourseDto();
        courseDto.setId(courseId);
        courseDto.setName("Test Course");
        courseDto.setPrice(0);

        accountCourse = AccountCourse.builder()
                .id(accountCourseId)
                .accountId(accountId)
                .courseId(courseId)
                .status(1)
                .purchaseDate(LocalDateTime.now())
                .completedLesson(0)
                .totalLesson(10)
                .completed(false)
                .progressPercent(0)
                .build();

        createAccountCourse = new CreateAccountCourse();
        createAccountCourse.setAccountId(accountId);
        createAccountCourse.setCourseId(courseId);
    }

    @Test
    @DisplayName("Should get account course by id successfully")
    void testGetAccountCourseById_Success() {
        // Given
        when(accountCourseRepository.findById(accountCourseId)).thenReturn(Optional.of(accountCourse));
        doNothing().when(accountCourseRepository).updateLastAccessed(any(UUID.class), any(LocalDateTime.class));

        // When
        AccountCourseDto result = accountCourseService.getAccountCourseById(accountCourseId);

        // Then
        assertNotNull(result);
        assertEquals(accountCourseId, result.getId());
        verify(accountCourseRepository).findById(accountCourseId);
        verify(accountCourseRepository).updateLastAccessed(any(UUID.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when account course not found")
    void testGetAccountCourseById_NotFound() {
        // Given
        when(accountCourseRepository.findById(accountCourseId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            accountCourseService.getAccountCourseById(accountCourseId);
        });
        verify(accountCourseRepository).findById(accountCourseId);
    }

    @Test
    @DisplayName("Should create account course successfully for free course")
    void testCreate_FreeCourse_Success() {
        // Given
        when(accountCourseRepository.existsByAccountIdAndCourseId(accountId, courseId)).thenReturn(false);
        when(courseService.getNoneDeleteCourseById(courseId)).thenReturn(courseDto);
        when(lessonRepository.countActiveLessonsByCourseId(courseId)).thenReturn(10);

        AccountCourse savedAccountCourse = AccountCourse.builder()
                .id(accountCourseId)
                .accountId(accountId)
                .courseId(courseId)
                .status(1)
                .purchaseDate(LocalDateTime.now())
                .completedLesson(0)
                .totalLesson(10)
                .completed(false)
                .progressPercent(0)
                .build();

        when(accountCourseRepository.save(any(AccountCourse.class))).thenReturn(savedAccountCourse);

        // When
        AccountCourseDto result = accountCourseService.create(createAccountCourse);

        // Then
        assertNotNull(result);
        assertEquals(accountCourseId, result.getId());
        verify(accountCourseRepository).existsByAccountIdAndCourseId(accountId, courseId);
        verify(courseService).getNoneDeleteCourseById(courseId);
        verify(accountCourseRepository).save(any(AccountCourse.class));
    }

    @Test
    @DisplayName("Should throw ConflictException when account course already exists")
    void testCreate_AlreadyExists() {
        // Given
        when(accountCourseRepository.existsByAccountIdAndCourseId(accountId, courseId)).thenReturn(true);

        // When & Then
        assertThrows(ConflictException.class, () -> {
            accountCourseService.create(createAccountCourse);
        });
        verify(accountCourseRepository).existsByAccountIdAndCourseId(accountId, courseId);
        verify(accountCourseRepository, never()).save(any(AccountCourse.class));
    }

    @Test
    @DisplayName("Should throw ConflictException when trying to create paid course")
    void testCreate_PaidCourse() {
        // Given
        courseDto.setPrice(100000);
        when(accountCourseRepository.existsByAccountIdAndCourseId(accountId, courseId)).thenReturn(false);
        when(courseService.getNoneDeleteCourseById(courseId)).thenReturn(courseDto);

        // When & Then
        assertThrows(ConflictException.class, () -> {
            accountCourseService.create(createAccountCourse);
        });
        verify(accountCourseRepository).existsByAccountIdAndCourseId(accountId, courseId);
        verify(courseService).getNoneDeleteCourseById(courseId);
        verify(accountCourseRepository, never()).save(any(AccountCourse.class));
    }

    @Test
    @DisplayName("Should get account courses by account id successfully")
    void testGetAccountCoursesByAccountId_Success() {
        // Given
        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<AccountCourse> accountCoursePage = new PageImpl<>(List.of(accountCourse), pageable, 1);

        when(accountCourseRepository.findActiveByAccountId(accountId, pageable)).thenReturn(accountCoursePage);

        // When
        PagedResult<AccountCourseDto> result = accountCourseService.getAccountCoursesByAccountId(accountId, page, size);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getData().size());
        verify(accountCourseRepository).findActiveByAccountId(accountId, pageable);
    }

    @Test
    @DisplayName("Should get account course by account id and course id successfully")
    void testGetByAccountIdAndCourseId_Success() {
        // Given
        when(accountCourseRepository.findByAccountIdAndCourseId(accountId, courseId))
                .thenReturn(Optional.of(accountCourse));

        // When
        AccountCourseDto result = accountCourseService.getByAccountIdAndCourseId(accountId, courseId);

        // Then
        assertNotNull(result);
        assertEquals(accountCourseId, result.getId());
        verify(accountCourseRepository).findByAccountIdAndCourseId(accountId, courseId);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when account course not found by account and course id")
    void testGetByAccountIdAndCourseId_NotFound() {
        // Given
        when(accountCourseRepository.findByAccountIdAndCourseId(accountId, courseId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            accountCourseService.getByAccountIdAndCourseId(accountId, courseId);
        });
        verify(accountCourseRepository).findByAccountIdAndCourseId(accountId, courseId);
    }

    @Test
    @DisplayName("Should delete account course successfully")
    void testDelete_Success() {
        // Given
        when(accountCourseRepository.findById(accountCourseId)).thenReturn(Optional.of(accountCourse));
        doNothing().when(accountCourseRepository).softDeleteById(accountCourseId);
        doNothing().when(accountLessonRepository).softDeleteByAccountIdAndCourseId(accountId, courseId);

        // When
        assertDoesNotThrow(() -> {
            accountCourseService.delete(accountCourseId);
        });

        // Then
        verify(accountCourseRepository).findById(accountCourseId);
        verify(accountCourseRepository).softDeleteById(accountCourseId);
        verify(accountLessonRepository).softDeleteByAccountIdAndCourseId(accountId, courseId);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when deleting non-existent account course")
    void testDelete_NotFound() {
        // Given
        when(accountCourseRepository.findById(accountCourseId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            accountCourseService.delete(accountCourseId);
        });
        verify(accountCourseRepository).findById(accountCourseId);
        verify(accountCourseRepository, never()).softDeleteById(any(UUID.class));
    }
}

