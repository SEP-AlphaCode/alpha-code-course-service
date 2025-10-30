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
@Table(name = "bundle")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Bundle extends BaseEntity {
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

    @Column(name = "discount_price")
    private Integer discountPrice;

    @Column(name = "cover_image", length = 512)
    private String coverImage;


    @OneToMany(mappedBy = "bundle", fetch = FetchType.LAZY)
    private List<CourseBundle> courseBundles;
}
