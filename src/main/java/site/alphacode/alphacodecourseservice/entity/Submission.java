package site.alphacode.alphacodecourseservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import site.alphacode.alphacodecourseservice.base.BaseEntity;

import java.util.UUID;

@Entity
@Table(name = "submission")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Submission extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "log_file_url", length = 255)
    private String logFileUrl;

    @Column(name = "video_url", length = 255)
    private String videoUrl;

    @Column(name = "account_lesson_id", nullable = false, columnDefinition = "uuid")
    private UUID accountLessonId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_lesson_id", insertable = false, updatable = false)
    private AccountLesson accountLesson;
}
