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
@Table(name = "lesson")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Lesson extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "slug", nullable = false, unique = true, length = 255)
    private String slug;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "video_url", length = 512)
    private String videoUrl;

    @Column(name = "duration", nullable = false)
    private Integer duration; // tính bằng giây

    @Column(name = "require_robot", nullable = false)
    private Boolean requireRobot = false;

    @Type(JsonType.class)
    @Column(name = "solution", columnDefinition = "jsonb")
    private JsonNode solution;

    @Column(name = "order_number", nullable = false)
    private Integer orderNumber;

    @Column(name = "type", nullable = false)
    private Integer type;

    @Column(name = "section_id", nullable = false, columnDefinition = "uuid")
    private UUID sectionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", insertable = false, updatable = false)
    private Section section;
}
