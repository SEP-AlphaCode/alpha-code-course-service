package site.alphacode.alphacodecourseservice.grpc.client;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import user.UserServiceGrpc;
import user.User.AccountInformation;
import user.User.GetAccountRequest;

@Service
public class UserServiceClient {

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub blockingStub;

    public AccountInformation getAccount(String accountId) {
        GetAccountRequest request = GetAccountRequest.newBuilder()
                .setAccountId(accountId)
                .build();
        return blockingStub.getAccount(request);
    }
}
