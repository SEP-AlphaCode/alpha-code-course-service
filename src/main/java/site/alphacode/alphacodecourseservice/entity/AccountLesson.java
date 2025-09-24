package site.alphacode.alphacodecourseservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.Set;
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

    @Column(name = "account_id", nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID accountId;

    @Column(name = "lesson_id", nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID lessonId;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", insertable = false, updatable = false)
    private Lesson lesson;

    @OneToMany(mappedBy = "accountLesson", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Submission> submissions;
}
