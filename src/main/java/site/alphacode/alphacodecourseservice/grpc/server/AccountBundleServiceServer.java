package site.alphacode.alphacodecourseservice.grpc.server;

import account_bundle.AccountBundle;
import account_bundle.AccountBundleServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import site.alphacode.alphacodecourseservice.service.AccountBundleService;

@GrpcService
@RequiredArgsConstructor
public class AccountBundleServiceServer extends AccountBundleServiceGrpc.AccountBundleServiceImplBase {
    private final AccountBundleService accountBundleService;

    @Override
    public void checkOwnBundle(AccountBundle.CheckOwnBundleRequest request, StreamObserver<AccountBundle.CheckOwnBundleResponse> responseObserver) {
        boolean isOwned = accountBundleService.getByAccountIdAndBundleId(
                java.util.UUID.fromString(request.getAccountId()),
                java.util.UUID.fromString(request.getBundleId())
        ) != null;

        AccountBundle.CheckOwnBundleResponse response = AccountBundle.CheckOwnBundleResponse.newBuilder()
                .setOwned(isOwned)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
