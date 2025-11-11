package site.alphacode.alphacodecourseservice.service.implement;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.alphacode.alphacodecourseservice.dto.request.StaffReviewRequest;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateSubmission;
import site.alphacode.alphacodecourseservice.dto.response.SubmissionDto;
import site.alphacode.alphacodecourseservice.entity.Submission;
import site.alphacode.alphacodecourseservice.enums.SubmissionEnum;
import site.alphacode.alphacodecourseservice.exception.BadRequestException;
import site.alphacode.alphacodecourseservice.exception.ResourceNotFoundException;
import site.alphacode.alphacodecourseservice.mapper.SubmissionMapper;
import site.alphacode.alphacodecourseservice.repository.AccountLessonRepository;
import site.alphacode.alphacodecourseservice.repository.SubmissionRepository;
import site.alphacode.alphacodecourseservice.service.CheckerService;
import site.alphacode.alphacodecourseservice.service.LessonService;
import site.alphacode.alphacodecourseservice.service.S3Service;
import site.alphacode.alphacodecourseservice.service.SubmissionService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionServiceImplement implements SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final S3Service s3Service;
    private final CheckerService checkerService;
    private final AccountLessonServiceImplement accountLessonService;
    private final AccountLessonRepository accountLessonRepository;
    private final LessonService lessonService;

    @Override
    @Cacheable(value = "submissionByAccountLessonId", key = "{#accountLessonId}")
    public SubmissionDto getByAccountLessonId(UUID accountLessonId) {
        var submission = submissionRepository
                .findTopByAccountLessonIdAndStatusOrderByCreatedDateDesc(accountLessonId, 1) // status = 1 = đã nộp
                .orElseThrow(() -> new BadRequestException(
                        "Không tìm thấy submission với accountLessonId: " + accountLessonId
                ));
        return SubmissionMapper.toDto(submission);
    }


    @Override
    @Transactional
    @CacheEvict(value = "submissionByAccountLessonId", allEntries = true)
    public SubmissionDto createSubmission(CreateSubmission request) {
        log.info("=== START createSubmission: accountLessonId={} ===", request.getAccountLessonId());

        if (request.getLogData() == null && request.getVideoFile() == null) {
            throw new BadRequestException("Phải gửi ít nhất logData hoặc videoFile");
        }

        JsonNode logData = request.getLogData();
        String videoUrl = null;

        if (request.getVideoFile() != null && !request.getVideoFile().isEmpty()) {
            try {
                log.info("Uploading video file...");
                String fileKey = "submissions/" + System.currentTimeMillis() + "_" +  request.getVideoFile().getOriginalFilename();
                videoUrl = s3Service.uploadStream(
                        request.getVideoFile().getInputStream(),
                        request.getVideoFile().getSize(),
                        fileKey,
                        request.getVideoFile().getContentType()
                );
                log.info("Video uploaded successfully: {}", videoUrl);
            } catch (IOException e) {
                log.error("Upload video failed", e);
                throw new RuntimeException("Upload video thất bại", e);
            }
        }

        log.info("Creating submission entity...");
        Submission submission = new Submission();

        submission.setAccountLessonId(request.getAccountLessonId());
        submission.setLogData(logData);
        submission.setVideoUrl(videoUrl);
        submission.setCreatedDate(LocalDateTime.now());
        submission.setStatus(1); // 1 = Đã nộp

        // Auto-check log nếu có
        if (logData != null) {
            log.info("LogData present, starting auto-check...");
            try {
                var accountLesson = accountLessonService.getAccountLessonWithLessonById(request.getAccountLessonId())
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy AccountLesson"));
                log.info("Found accountLesson: lessonId={}, completedAt={}", accountLesson.getLessonId(), accountLesson.getCompletedAt());

                var lesson = lessonService.getLessonWithSolutionById(accountLesson.getLessonId());
                if(lesson.getSolution() == null){
                    log.warn("Lesson has no solution");
                    throw new BadRequestException("Bài học chưa có bài giải, không thể chấm tự động");
                }

                log.info("Calling checkerService.autoCheck()...");
                boolean isPass = checkerService.autoCheck(submission);
                log.info("AutoCheck result: isPass={}", isPass);

                if (isPass) {
                    submission.setStatus(2); // 2 = PASSED
                    log.info("Submission PASSED, status set to 2");

                    // Cập nhật hoàn thành bài học
                    if (accountLesson.getCompletedAt() == null) {
                        log.info("Lesson not completed yet, calling markComplete...");
                        try {
                            accountLessonService.markComplete(request.getAccountLessonId());
                            log.info("markComplete completed successfully");
                        } catch (Exception e) {
                            log.error("Error in markComplete: {}", e.getMessage(), e);
                            // Không throw exception để submission vẫn được save
                        }
                    } else {
                        log.info("Lesson already completed at: {}", accountLesson.getCompletedAt());
                    }
                } else {
                    submission.setStatus(3); // 3 = FAILED
                    log.info("Submission FAILED, status set to 3");
                }
            } catch (Exception e) {
                log.error("ERROR during auto-check process: {}", e.getMessage(), e);
                // Set status to error but still save submission
                submission.setStatus(5); // 5 = ERROR
                log.warn("Auto-check failed, submission will be saved with ERROR status");
            }
        } else {
            submission.setStatus(4);
            log.info("No logData, status set to 4 (PENDING_REVIEW)");
        }

        log.info("=== BEFORE SAVE: Tạo submission cho accountLessonId={} với status={} ===", request.getAccountLessonId(), submission.getStatus());

        Submission saved = submissionRepository.save(submission);
        log.info("=== AFTER SAVE: Submission saved with id={} ===", saved.getId());

        SubmissionDto result = SubmissionMapper.toDto(saved);
        log.info("=== END createSubmission: Returning SubmissionDto ===");
        return result;
    }

    @Override
    @Transactional
    @CacheEvict(value = "submissionByAccountLessonId", allEntries = true)
    public SubmissionDto reviewSubmission(UUID submissionId, StaffReviewRequest request) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy submission: " + submissionId));

        if (!submission.getStatus().equals(SubmissionEnum.PENDING_REVIEW.getCode())) {
            throw new BadRequestException("Submission này không ở trạng thái cần review");
        }

        if (request.isApproved()) {
            submission.setStatus(SubmissionEnum.PASS_HUMAN.getCode());
        } else {
            submission.setStatus(SubmissionEnum.FAIL_HUMAN.getCode());
        }

        submission.setLastUpdated(LocalDateTime.now());
        submission.setStaffComment(request.getComment());

        Submission saved = submissionRepository.save(submission);

        // Nếu PASS thì cập nhật tiến độ course (giống auto check)
        if (request.isApproved()) {
            accountLessonService.markComplete(submission.getAccountLessonId());
        }

        return SubmissionMapper.toDto(saved);
    }

}
