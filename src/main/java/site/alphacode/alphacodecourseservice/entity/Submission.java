package site.alphacode.alphacodecourseservice.entity;

import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import site.alphacode.alphacodecourseservice.base.BaseEntity;

import java.util.UUID;

@Entity
@Table(name = "submission")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Submission extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Type(JsonType.class)
    @Column(name = "log_data", columnDefinition = "jsonb")
    private JsonNode logData;

    @Column(name = "video_url", length = 255)
    private String videoUrl;

    @Column(name = "account_lesson_id", nullable = false, columnDefinition = "uuid")
    private UUID accountLessonId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_lesson_id", insertable = false, updatable = false)
    private AccountLesson accountLesson;
}
