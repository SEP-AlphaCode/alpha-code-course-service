package site.alphacode.alphacodecourseservice.grpc.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateSubmission;
import site.alphacode.alphacodecourseservice.dto.response.SubmissionDto;
import site.alphacode.alphacodecourseservice.service.SubmissionService;
import submission.Submission;
import submission.SubmissionServiceGrpc;

import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class SubmissionServiceServer extends SubmissionServiceGrpc.SubmissionServiceImplBase {

    private final ObjectMapper objectMapper;
    private final SubmissionService submissionService;

    @Override
    @Transactional
    public void submitRobotLogs(Submission.RobotSubmissionRequest request, StreamObserver<Submission.RobotSubmissionResponse> responseObserver) {
        String robotId = request.getRobotId();
        String accountLessonId = request.getAccountLessonId();
        String logDataJson = request.getLogDataJson();

        log.info("📩 [gRPC] Received logs from robot={} for accountLessonId={}", robotId, accountLessonId);

        try {
            // Parse JSON log
            JsonNode logData = objectMapper.readTree(logDataJson);

            // Gọi hàm createSubmission có sẵn để lưu và chấm
            CreateSubmission createRequest = CreateSubmission.builder()
                    .accountLessonId(UUID.fromString(accountLessonId))
                    .logData(logData)
                    .videoFile(null) // gRPC không truyền file
                    .build();

            SubmissionDto result = submissionService.createSubmission(createRequest);

            // Trả response OK
            Submission.RobotSubmissionResponse response = Submission.RobotSubmissionResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Submission created successfully with status=" + result.getStatus())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("Created submission for accountLessonId={} (status={})", accountLessonId, result.getStatus());

        } catch (Exception e) {
            log.error("Error while creating submission for accountLessonId={}: {}", accountLessonId, e.getMessage(), e);

            Submission.RobotSubmissionResponse response = Submission.RobotSubmissionResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Error: " + e.getMessage())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
