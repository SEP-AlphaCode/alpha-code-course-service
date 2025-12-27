package site.alphacode.alphacodecourseservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import site.alphacode.alphacodecourseservice.dto.request.StaffReviewRequest;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateSubmission;
import site.alphacode.alphacodecourseservice.dto.response.SubmissionDto;
import site.alphacode.alphacodecourseservice.entity.Submission;
import site.alphacode.alphacodecourseservice.enums.SubmissionEnum;
import site.alphacode.alphacodecourseservice.exception.BadRequestException;
import site.alphacode.alphacodecourseservice.exception.ResourceNotFoundException;
import site.alphacode.alphacodecourseservice.repository.SubmissionRepository;
import site.alphacode.alphacodecourseservice.service.CheckerService;
import site.alphacode.alphacodecourseservice.service.S3Service;
import site.alphacode.alphacodecourseservice.service.implement.AccountLessonServiceImplement;
import site.alphacode.alphacodecourseservice.service.implement.SubmissionServiceImplement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SubmissionService Unit Tests")
class SubmissionServiceTest {

    @Mock
    private SubmissionRepository submissionRepository;

    @InjectMocks
    private SubmissionServiceImplement submissionService;

    private UUID submissionId;
    private UUID accountLessonId;
    private Submission submission;
    private CreateSubmission createSubmission;
    private StaffReviewRequest reviewRequest;

    @BeforeEach
    void setUp() {
        submissionId = UUID.randomUUID();
        accountLessonId = UUID.randomUUID();
        submission = Submission.builder()
                .id(submissionId)
                .accountLessonId(accountLessonId)
                .status(SubmissionEnum.PENDING_REVIEW.getCode())
                .createdDate(LocalDateTime.now())
                .build();

        createSubmission = new CreateSubmission();
        createSubmission.setAccountLessonId(accountLessonId);
        createSubmission.setVideoUrl("https://example.com/video.mp4");

        reviewRequest = new StaffReviewRequest();
        reviewRequest.setApproved(true);
        reviewRequest.setComment("Good work!");
    }

    @Test
    @DisplayName("Should get submission by account lesson id successfully")
    void testGetByAccountLessonId_Success() {
        // Given
        when(submissionRepository.findTopByAccountLessonIdOrderByCreatedDateDesc(accountLessonId))
                .thenReturn(Optional.of(submission));

        // When
        SubmissionDto result = submissionService.getByAccountLessonId(accountLessonId);

        // Then
        assertNotNull(result);
        assertEquals(submissionId, result.getId());
        verify(submissionRepository).findTopByAccountLessonIdOrderByCreatedDateDesc(accountLessonId);
    }

    @Test
    @DisplayName("Should throw BadRequestException when submission not found")
    void testGetByAccountLessonId_NotFound() {
        // Given
        when(submissionRepository.findTopByAccountLessonIdOrderByCreatedDateDesc(accountLessonId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(BadRequestException.class, () -> {
            submissionService.getByAccountLessonId(accountLessonId);
        });
        verify(submissionRepository).findTopByAccountLessonIdOrderByCreatedDateDesc(accountLessonId);
    }

    @Test
    @DisplayName("Should create submission with video URL successfully")
    void testCreateSubmission_WithVideoUrl_Success() {
        // Given
        createSubmission.setVideoUrl("https://example.com/video.mp4");
        createSubmission.setLogData(null);

        Submission savedSubmission = Submission.builder()
                .id(submissionId)
                .accountLessonId(accountLessonId)
                .videoUrl("https://example.com/video.mp4")
                .status(SubmissionEnum.PENDING_REVIEW.getCode())
                .createdDate(LocalDateTime.now())
                .build();

        when(submissionRepository.save(any(Submission.class))).thenReturn(savedSubmission);

        // When
        SubmissionDto result = submissionService.createSubmission(createSubmission);

        // Then
        assertNotNull(result);
        assertEquals(submissionId, result.getId());
        verify(submissionRepository).save(any(Submission.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException when both logData and videoUrl are null")
    void testCreateSubmission_NoData() {
        // Given
        createSubmission.setLogData(null);
        createSubmission.setVideoUrl(null);

        // When & Then
        assertThrows(BadRequestException.class, () -> {
            submissionService.createSubmission(createSubmission);
        });
        verify(submissionRepository, never()).save(any(Submission.class));
    }

    @Test
    @DisplayName("Should review submission successfully when rejected")
    void testReviewSubmission_Rejected_Success() {
        // Given
        submission.setStatus(SubmissionEnum.PENDING_REVIEW.getCode());
        reviewRequest.setApproved(false);

        when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(submission));
        when(submissionRepository.save(any(Submission.class))).thenReturn(submission);

        // When
        SubmissionDto result = submissionService.reviewSubmission(submissionId, reviewRequest);

        // Then
        assertNotNull(result);
        assertEquals(SubmissionEnum.FAIL_HUMAN.getCode(), submission.getStatus());
        verify(submissionRepository).findById(submissionId);
        verify(submissionRepository).save(any(Submission.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when submission not found for review")
    void testReviewSubmission_NotFound() {
        // Given
        when(submissionRepository.findById(submissionId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            submissionService.reviewSubmission(submissionId, reviewRequest);
        });
        verify(submissionRepository).findById(submissionId);
        verify(submissionRepository, never()).save(any(Submission.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException when submission is not in reviewable state")
    void testReviewSubmission_InvalidStatus() {
        // Given
        submission.setStatus(SubmissionEnum.PASS_HUMAN.getCode());
        when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(submission));

        // When & Then
        assertThrows(BadRequestException.class, () -> {
            submissionService.reviewSubmission(submissionId, reviewRequest);
        });
        verify(submissionRepository).findById(submissionId);
        verify(submissionRepository, never()).save(any(Submission.class));
    }
}

