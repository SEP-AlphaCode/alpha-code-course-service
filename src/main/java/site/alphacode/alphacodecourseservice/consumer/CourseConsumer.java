package site.alphacode.alphacodecourseservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateAccountCourse;
import site.alphacode.alphacodecourseservice.entity.AccountCourse;
import site.alphacode.alphacodecourseservice.exception.ConflictException;
import site.alphacode.alphacodecourseservice.service.AccountCourseService;
import site.alphacode.alphacodecourseservice.service.CourseService;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CourseConsumer {

    private final AccountCourseService accountCourseService;

    @RabbitListener(
            queues = "course.create.queue",
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void handleCoursePayment(Map<String, Object> message) {
        Long orderCode = ((Number) message.get("orderCode")).longValue();
        UUID accountId = UUID.fromString((String) message.get("accountId"));
        String courseId = (String) message.get("courseId");

        log.info("Received course payment: orderCode={}, accountId={}, courseId={}", orderCode, accountId, courseId);

        CreateAccountCourse createAccountCourse = new CreateAccountCourse();
        createAccountCourse.setAccountId(accountId);
        createAccountCourse.setCourseId(UUID.fromString(courseId));

        try {
            accountCourseService.create(createAccountCourse);
            log.info("Course purchase recorded successfully for accountId={}, courseId={}", accountId, courseId);
        } catch (ConflictException e) {
            // Bỏ qua nếu khóa học đã được mua trước đó
            log.warn("Course already purchased for accountId={}, courseId={}, skipping.", accountId, courseId);
        } catch (Exception e) {
            log.error("Error processing course payment for accountId={}, courseId={}", accountId, courseId, e);
            // Có thể rethrow nếu muốn Spring retry
            throw e;
        }
    }


}
