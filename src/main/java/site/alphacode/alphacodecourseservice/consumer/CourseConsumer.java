package site.alphacode.alphacodecourseservice.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import site.alphacode.alphacodecourseservice.service.CourseService;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CourseConsumer {

    private final CourseService courseService;

    @RabbitListener(queues = "course.create.queue")
    public void handleCoursePayment(Map<String, Object> message) {
        Long orderCode = ((Number) message.get("orderCode")).longValue();
        UUID accountId = UUID.fromString((String) message.get("accountId"));
        String courseId = (String) message.get("courseId");

        System.out.println("Received course payment message: " + message);

        // Tạo khóa học cho user
        courseService.createCourseForUser(accountId, courseId, orderCode);
    }
}
