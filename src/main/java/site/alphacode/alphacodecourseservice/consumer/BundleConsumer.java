package site.alphacode.alphacodecourseservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateAccountBundle;
import site.alphacode.alphacodecourseservice.exception.ConflictException;
import site.alphacode.alphacodecourseservice.service.AccountBundleService;
import site.alphacode.alphacodecourseservice.service.AccountCourseService;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BundleConsumer {
    private final AccountCourseService accountCourseService;
    private final AccountBundleService accountBundleService;

    @RabbitListener(
            queues = "bundle.create.queue",
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void handleBundlePayment(Map<String, Object> message) {
        Long orderCode = ((Number) message.get("orderCode")).longValue();
        UUID accountId = UUID.fromString((String) message.get("accountId"));
        UUID bundleId = UUID.fromString((String) message.get("bundleId"));

        log.info("Received course payment: orderCode={}, accountId={}, bundleId={}", orderCode, accountId, bundleId);

        try {
            accountCourseService.createFromBundle(accountId, bundleId);
            CreateAccountBundle createAccountBundle = new CreateAccountBundle();
            createAccountBundle.setAccountId(accountId);
            createAccountBundle.setBundleId(bundleId);
            accountBundleService.create(createAccountBundle);
            log.info("Course purchase recorded successfully for accountId={}, bundleId={}", accountId, bundleId);
        } catch (ConflictException e) {
            // Bỏ qua nếu khóa học đã được mua trước đó
            log.warn("Course already purchased for accountId={}, bundleId={}, skipping.", accountId, bundleId);
        } catch (Exception e) {
            log.error("Error processing course payment for accountId={}, bundleId={}", accountId, bundleId, e);
            // Có thể rethrow nếu muốn Spring retry
            throw e;
        }
    }
}
