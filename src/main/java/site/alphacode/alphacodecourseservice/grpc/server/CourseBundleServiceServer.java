package site.alphacode.alphacodecourseservice.grpc.server;

import course_bundle.CourseBundleServiceGrpc;
import course_bundle.GetCourseIdsByBundleRequest;
import course_bundle.GetCourseIdsByBundleResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import site.alphacode.alphacodecourseservice.repository.CourseBundleRepository;

import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class CourseBundleServiceServer  extends CourseBundleServiceGrpc.CourseBundleServiceImplBase {
    private final CourseBundleRepository courseBundleRepository;

    @Override
    public void getCourseIdsByBundle(GetCourseIdsByBundleRequest request,
                                     StreamObserver<GetCourseIdsByBundleResponse> responseObserver) {
        try {
            UUID bundleId = UUID.fromString(request.getBundleId());

            var courseIds = courseBundleRepository.findCourseIdsByBundleId(bundleId);

            GetCourseIdsByBundleResponse response = GetCourseIdsByBundleResponse.newBuilder()
                    .addAllCourseIds(courseIds.stream().map(UUID::toString).toList())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
}
