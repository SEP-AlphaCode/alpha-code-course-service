package site.alphacode.alphacodecourseservice.grpc.client;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import io.grpc.StatusRuntimeException;
import user.UserServiceGrpc;
import user.User.AccountInformation;
import user.User.GetAccountRequest;

@Service
@Slf4j
public class UserServiceClient {

    @GrpcClient("alpha-user-service")
    private UserServiceGrpc.UserServiceBlockingStub blockingStub;

    public AccountInformation getAccount(String accountId) {
        log.info("Calling gRPC UserService.getAccount with accountId={}", accountId);

        GetAccountRequest request = GetAccountRequest.newBuilder()
                .setAccountId(accountId)
                .build();

        try {
            AccountInformation response = blockingStub.getAccount(request);
            log.info("Received response from UserService for accountId={}: {}", accountId, response);
            return response;
        } catch (StatusRuntimeException e) {
            log.error("gRPC call failed for accountId={}: {}", accountId, e.getStatus(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error calling UserService for accountId={}", accountId, e);
            throw e;
        }
    }
}
