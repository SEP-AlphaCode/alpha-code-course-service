package site.alphacode.alphacodecourseservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "accounts_course")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountCourse {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "account_course_id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @NotNull(message = "Account ID là bắt buộc")
    @Column(name = "account_id", nullable = false, columnDefinition = "uuid")
    private UUID accountId;

    @NotNull(message = "Course ID là bắt buộc")
    @Column(name = "course_id", nullable = false, columnDefinition = "uuid")
    private UUID courseId;

    @NotNull(message = "Ngày mua là bắt buộc")
    @Column(name = "purchase_date", nullable = false)
    private LocalDateTime purchaseDate;

    @NotNull(message = "Trạng thái hoàn thành là bắt buộc")
    @Column(name = "completed", nullable = false)
    private Boolean completed;

    @NotNull(message = "Tổng số bài học là bắt buộc")
    @Min(value = 1, message = "Tổng số bài học phải ít nhất 1")
    @Column(name = "total_lesson", nullable = false)
    private Integer totalLesson;

    @NotNull(message = "Số bài học đã hoàn thành là bắt buộc")
    @Min(value = 0, message = "Bài học hoàn thành không được nhỏ hơn 0")
    @Column(name = "completed_lesson", nullable = false)
    private Integer completedLesson;

    @NotNull(message = "Tiến độ là bắt buộc")
    @Min(value = 0, message = "Tiến độ không được nhỏ hơn 0")
    @Max(value = 100, message = "Tiến độ không được lớn hơn 100")
    @Column(name = "progress_percent", nullable = false)
    private Integer progressPercent;

    @NotNull(message = "Trạng thái là bắt buộc")
    @Column(name = "status", nullable = false)
    private Integer status; // có thể map enum sau này

    @Column(name = "last_accessed")
    private LocalDateTime lastAccessed;
}
