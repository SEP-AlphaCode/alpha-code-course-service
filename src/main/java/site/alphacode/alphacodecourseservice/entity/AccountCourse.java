package site.alphacode.alphacodecourseservice.entity;

import jakarta.persistence.*;
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

    @Column(name = "account_id", nullable = false, columnDefinition = "uuid")
    private UUID accountId;

    @Column(name = "course_id", nullable = false, columnDefinition = "uuid")
    private UUID courseId;

    @Column(name = "purchase_date", nullable = false)
    private LocalDateTime purchaseDate;

    @Column(name = "completed", nullable = false)
    private Boolean completed;

    @Column(name = "total_lesson", nullable = false)
    private Integer totalLesson;

    @Column(name = "completed_lesson", nullable = false)
    private Integer completedLesson;

    @Column(name = "progress_percent", nullable = false)
    private Integer progressPercent;

    @Column(name = "status", nullable = false)
    private Integer status; // có thể map enum sau này

    @Column(name = "last_accessed")
    private LocalDateTime lastAccessed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    private Course course;
}
