package site.alphacode.alphacodecourseservice.service.implement;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
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
    @CachePut(value = "submissionByAccountLessonId", key = "{#request.accountLessonId}")
    public SubmissionDto createSubmission(CreateSubmission request) {
        if (request.getLogData() == null && request.getVideoFile() == null) {
            throw new BadRequestException("Phải gửi ít nhất logData hoặc videoFile");
        }

        JsonNode logData = request.getLogData();
        String videoUrl = null;

        if (request.getVideoFile() != null && !request.getVideoFile().isEmpty()) {
            try {
                String fileKey = "submissions/" + System.currentTimeMillis() + "_" +  request.getVideoFile().getOriginalFilename();
                videoUrl = s3Service.uploadStream(
                        request.getVideoFile().getInputStream(),
                        request.getVideoFile().getSize(),
                        fileKey,
                        request.getVideoFile().getContentType()
                );
            } catch (IOException e) {
                throw new RuntimeException("Upload video thất bại", e);
            }
        }

        Submission submission = Submission.builder()
                .accountLessonId(request.getAccountLessonId())
                .logData(logData)
                .videoUrl(videoUrl)
                .createdDate(LocalDateTime.now())
                .status(1)
                .build();

        // Auto-check log nếu có
        if (logData != null) {
            var accountLesson = accountLessonService.getAccountLessonWithLessonById(request.getAccountLessonId()) .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy AccountLesson"));
            var lesson = lessonService.getLessonWithSolutionById(accountLesson.getLessonId());
            if(lesson.getSolution() == null){
                throw new BadRequestException("Bài học chưa có bài giải, không thể chấm tự động");
            }

            boolean isPass = checkerService.autoCheck(submission);
            if (isPass) {
                submission.setStatus(2); // 2 = PASSED
                // Cập nhật hoàn thành bài học
                if (accountLesson.getCompletedAt() == null) {
                    try {
                        accountLessonService.markComplete(request.getAccountLessonId());
                    } catch (IllegalStateException e) {
                        log.warn("Bài học đã được hoàn thành trước đó: {}", e.getMessage());
                    } catch (Exception e) {
                        log.error("Lỗi khi cập nhật hoàn thành bài học: {}", e.getMessage(), e);
                        // Không throw exception, vẫn save submission
                    }
                }
            } else {
                submission.setStatus(3); // 3 = FAILED
            }
        } else {
            submission.setStatus(4);
        }

        log.info("Tạo submission cho accountLessonId={} với status={}", request.getAccountLessonId(), submission.getStatus());

        Submission saved = submissionRepository.save(submission);
        return SubmissionMapper.toDto(saved);
    }

    @Override
    @Transactional
    @CachePut(value = "submissionByAccountLessonId", key = "{#submissionId}")
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
