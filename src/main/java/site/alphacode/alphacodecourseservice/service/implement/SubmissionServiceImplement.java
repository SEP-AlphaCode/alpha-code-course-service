package site.alphacode.alphacodecourseservice.service.implement;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateSubmission;
import site.alphacode.alphacodecourseservice.dto.response.SubmissionDto;
import site.alphacode.alphacodecourseservice.entity.Submission;
import site.alphacode.alphacodecourseservice.exception.BadRequestException;
import site.alphacode.alphacodecourseservice.mapper.SubmissionMapper;
import site.alphacode.alphacodecourseservice.repository.SubmissionRepository;
import site.alphacode.alphacodecourseservice.service.S3Service;
import site.alphacode.alphacodecourseservice.service.SubmissionService;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubmissionServiceImplement implements SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final S3Service s3Service;

    @Override
    public SubmissionDto createSubmission(CreateSubmission request) {
        if (request.getLogData() == null && request.getVideoFile() == null) {
            throw new BadRequestException("Phải gửi ít nhất logData hoặc videoFile");
        }

        JsonNode logData = null;
        String videoUrl = null;

        // Nếu có logData (robot gửi JSON)
        if (request.getLogData() != null) {
            logData = request.getLogData();
        }

        // Nếu có videoFile
        MultipartFile videoFile = request.getVideoFile();
        if (videoFile != null && !videoFile.isEmpty()) {
            try {
                // upload lên storage (S3, local,...)
                String fileKey = "submissions/" + videoFile.getOriginalFilename();
                videoUrl = s3Service.uploadBytes(
                        videoFile.getBytes(),
                        fileKey,
                        videoFile.getContentType()
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
                .lastUpdated(null)
                .status(1) // Mặc định là 1 - Đã nộp
                .build();

        var savedSubmission = submissionRepository.save(submission);

        return SubmissionMapper.toDto(savedSubmission);
    }
}
