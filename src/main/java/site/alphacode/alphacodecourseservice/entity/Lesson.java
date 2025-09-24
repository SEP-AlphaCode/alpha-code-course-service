package site.alphacode.alphacodecourseservice.entity;

import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import site.alphacode.alphacodecourseservice.base.BaseEntity;

import java.util.UUID;

@Entity
@Table(name = "lesson")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Lesson extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "content_url", length = 500)
    private String contentUrl;

    @Column(name = "content_type", length = 100, nullable = false)
    private String contentType;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "duration", nullable = false)
    private Integer duration; // tính bằng giây

    @Column(name = "require_robot", nullable = false)
    private Boolean requireRobot = false;

    @Type(JsonType.class)
    @Column(name = "solution", nullable = false)
    private JsonNode solution;

    @Column(name = "order_number", nullable = false)
    private Integer orderNumber;

    @Column(name = "course_id", nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID courseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    private Course course;
}
