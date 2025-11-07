package site.alphacode.alphacodecourseservice.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class CourseProducer implements CommandLineRunner {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitAdmin rabbitAdmin;


    public void run(String... args) {
        System.out.println("RabbitMQ connected: " + rabbitTemplate.getConnectionFactory().getHost());
        rabbitAdmin.initialize();
    }

    public CourseProducer(RabbitTemplate rabbitTemplate, RabbitAdmin rabbitAdmin) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitAdmin = rabbitAdmin;
    }

    /**
     * Gửi message khi user hoàn thành khóa học
     * @param accountId ID người dùng
     * @param courseId ID khóa học
     */
    public void sendCourseCompletedMessage(String accountId, String courseId, String courseName) {
        // Tạo payload
        Map<String, Object> payload = Map.of(
                "accountId", accountId,
                "courseId", courseId,
                "courseName", courseName
        );

        // Gửi message
        rabbitTemplate.convertAndSend(
                "notification.exchange", // exchange
                "course.completed",      // routing key
                payload                  // dữ liệu gửi
        );

        log.info("Đã gửi message hoàn thành khóa học: userId={}, courseId={}", accountId, courseId);
    }
}
