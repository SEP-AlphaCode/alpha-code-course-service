package site.alphacode.alphacodecourseservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import site.alphacode.alphacodecourseservice.base.BaseEntity;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "category")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "image_url", length = 512)
    private String imageUrl;

    @Column(name = "slug", nullable = false, length = 255, unique = true)
    private String slug;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private Set<Course> courses;
}
