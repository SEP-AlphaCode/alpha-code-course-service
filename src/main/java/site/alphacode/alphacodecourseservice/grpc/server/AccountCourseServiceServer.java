package site.alphacode.alphacodecourseservice.grpc.server;

import account_course.AccountCourse;
import account_course.AccountCourseServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import site.alphacode.alphacodecourseservice.repository.AccountCourseRepository;

import java.util.List;
import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class AccountCourseServiceServer extends AccountCourseServiceGrpc.AccountCourseServiceImplBase {
    private final AccountCourseRepository accountCourseRepository;

    @Override
    public void checkOwnCourse(AccountCourse.CheckOwnCourseRequest request, StreamObserver<AccountCourse.CheckOwnCourseResponse> responseObserver) {
        boolean owned = accountCourseRepository.existsByAccountIdAndCourseId(
                UUID.fromString(request.getAccountId()),
                UUID.fromString(request.getCourseId())
        );

        var response = AccountCourse.CheckOwnCourseResponse.newBuilder()
                .setOwned(owned)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void checkOwnCoursesInBundle(AccountCourse.CheckOwnCoursesInBundleRequest request,
                                        StreamObserver<AccountCourse.CheckOwnCoursesInBundleResponse> responseObserver) {
        List    <UUID> courseIds = request.getCourseIdsList().stream().map(UUID::fromString).toList();
        List<UUID> owned = accountCourseRepository.findOwnedCourseIds(UUID.fromString(request.getAccountId()), courseIds);

        var response = AccountCourse.CheckOwnCoursesInBundleResponse.newBuilder()
                .addAllOwnedCourseIds(owned.stream().map(UUID::toString).toList())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
