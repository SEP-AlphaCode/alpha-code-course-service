package site.alphacode.alphacodecourseservice.grpc.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import user.UserServiceGrpc;
import user.User.AccountInformation;
import user.User.GetAccountRequest;

import jakarta.annotation.PreDestroy;

@Service
public class UserServiceClient {

    private final ManagedChannel channel;
    private final UserServiceGrpc.UserServiceBlockingStub blockingStub;

    public UserServiceClient(
            @Value("${grpc.user-service.base-url}") String userServiceUrl,
            @Value("${grpc.user-service.port}") int userServicePort
    ) {
        this.channel = ManagedChannelBuilder.forAddress(userServiceUrl, userServicePort)
                .usePlaintext() // <-- chạy không TLS
                .build();
        this.blockingStub = UserServiceGrpc.newBlockingStub(channel);
    }

    public AccountInformation getAccount(String accountId) {
        GetAccountRequest request = GetAccountRequest.newBuilder()
                .setAccountId(accountId)
                .build();
        return blockingStub.getAccount(request);
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }
}
