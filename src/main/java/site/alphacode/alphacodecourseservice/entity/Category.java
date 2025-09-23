package site.alphacode.alphacodecourseservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import site.alphacode.alphacodecourseservice.baseentity.BaseEntity;

import java.util.List;
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

    @NotBlank(message = "Tên danh mục là bắt buộc")
    @Size(max = 255, message = "Tên danh mục không được vượt quá 255 ký tự")
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @NotBlank(message = "Mô tả danh mục là bắt buộc")
    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Size(max = 512, message = "Đường dẫn ảnh không được vượt quá 512 ký tự")
    @Column(name = "image_url", length = 512)
    private String imageUrl;

    @NotBlank(message = "Slug là bắt buộc")
    @Size(max = 255, message = "Slug không được vượt quá 255 ký tự")
    @Column(name = "slug", nullable = false, length = 255, unique = true)
    private String slug;
}

