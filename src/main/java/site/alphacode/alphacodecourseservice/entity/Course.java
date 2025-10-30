package site.alphacode.alphacodecourseservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import site.alphacode.alphacodecourseservice.base.BaseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "course")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Course extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "require_license", nullable = false)
    private Boolean requireLicense;

    @Column(name = "level", nullable = false)
    private Integer level;

    @Column(name = "total_lessons", nullable = false)
    private Integer totalLessons;

    @Column(name = "total_duration", nullable = false)
    private Integer totalDuration; // tính bằng giây

    @Column(name = "image_url", length = 512)
    private String imageUrl;

    @Column(name = "slug", nullable = false, unique = true, length = 255)
    private String slug;

    @Column(name = "category_id", nullable = false, columnDefinition = "uuid")
    private UUID categoryId;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private List<CourseBundle> courseBundles;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private List<Section> sections;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;
}
