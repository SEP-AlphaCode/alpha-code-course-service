package site.alphacode.alphacodecourseservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import site.alphacode.alphacodecourseservice.baseentity.BaseEntity;

import java.util.UUID;

@Entity
@Table(
        name = "course_bundle",
        uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "bundle_id"})
)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseBundle extends BaseEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @NotNull(message = "Course ID là bắt buộc")
    @Column(name = "course_id", nullable = false, columnDefinition = "uuid")
    private UUID courseId;

    @NotNull(message = "Bundle ID là bắt buộc")
    @Column(name = "bundle_id", nullable = false, columnDefinition = "uuid")
    private UUID bundleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bundle_id", insertable = false, updatable = false)
    private Bundle bundle;
}

