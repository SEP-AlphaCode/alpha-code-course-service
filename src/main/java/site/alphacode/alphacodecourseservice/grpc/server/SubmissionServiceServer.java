package site.alphacode.alphacodecourseservice.grpc.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import submission.SubmissionServiceGrpc;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class SubmissionServiceServer extends SubmissionServiceGrpc.SubmissionServiceImplBase {

}
