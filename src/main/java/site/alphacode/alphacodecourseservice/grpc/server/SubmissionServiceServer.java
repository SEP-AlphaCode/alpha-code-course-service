package site.alphacode.alphacodecourseservice.grpc.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
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
    public void submitRobotLogs(Submission.RobotSubmissionRequest request, StreamObserver<Submission.RobotSubmissionResponse> responseObserver) {
        String robotId = request.getRobotId();
        String accountLessonId = request.getAccountLessonId();
        String logDataJson = request.getLogDataJson();

        log.info("=== [gRPC SERVER] START: Received logs from robot={} for accountLessonId={} ===", robotId, accountLessonId);
        log.info("[gRPC SERVER] LogData length: {} bytes", logDataJson != null ? logDataJson.length() : 0);

        try {
            log.info("[gRPC SERVER] Parsing JSON log...");
            // Parse JSON log
            JsonNode logData = objectMapper.readTree(logDataJson);
            log.info("[gRPC SERVER] JSON parsed successfully, nodes: {}", logData.size());

            // Gọi hàm createSubmission có sẵn để lưu và chấm
            log.info("[gRPC SERVER] Building CreateSubmission request...");
            CreateSubmission createRequest = CreateSubmission.builder()
                    .accountLessonId(UUID.fromString(accountLessonId))
                    .logData(logData)
                    .videoFile(null) // gRPC không truyền file
                    .build();

            log.info("[gRPC SERVER] Calling submissionService.createSubmission for accountLessonId={}", accountLessonId);
            SubmissionDto result = submissionService.createSubmission(createRequest);
            log.info("[gRPC SERVER] createSubmission returned successfully with submissionId={}, status={}", result.getId(), result.getStatus());

            // Trả response OK
            Submission.RobotSubmissionResponse response = Submission.RobotSubmissionResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Submission created successfully with status=" + result.getStatus())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("=== [gRPC SERVER] SUCCESS: Created submission id={} for accountLessonId={} (status={}) ===", result.getId(), accountLessonId, result.getStatus());

        } catch (Exception e) {
            log.error("=== [gRPC SERVER] ERROR: Failed creating submission for accountLessonId={} ===", accountLessonId);
            log.error("[gRPC SERVER] Exception type: {}", e.getClass().getName());
            log.error("[gRPC SERVER] Exception message: {}", e.getMessage());
            log.error("[gRPC SERVER] Stack trace:", e);

            Submission.RobotSubmissionResponse response = Submission.RobotSubmissionResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Error: " + e.getMessage())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
