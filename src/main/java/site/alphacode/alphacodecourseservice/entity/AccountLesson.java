package site.alphacode.alphacodecourseservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "account_lesson")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountLesson {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @NotNull(message = "Tài khoản là bắt buộc")
    @Column(name = "account_id", nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID accountId;

    @NotNull(message = "Bài học là bắt buộc")
    @Column(name = "lesson_id", nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID lessonId;

    @NotNull(message = "Trạng thái là bắt buộc")
    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}

