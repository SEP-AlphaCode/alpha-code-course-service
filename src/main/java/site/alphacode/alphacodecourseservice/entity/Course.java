package site.alphacode.alphacodecourseservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import site.alphacode.alphacodecourseservice.baseentity.BaseEntity;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "course")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Course extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @NotBlank(message = "Tên khóa học là bắt buộc")
    @Size(max = 255, message = "Tên khóa học không được vượt quá 255 ký tự")
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @NotBlank(message = "Mô tả khóa học là bắt buộc")
    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @NotNull(message = "Giá khóa học là bắt buộc")
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá phải lớn hơn hoặc bằng 0")
    @Digits(integer = 8, fraction = 2, message = "Giá phải là số hợp lệ, tối đa 8 chữ số nguyên và 2 chữ số thập phân")
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull(message = "Trạng thái yêu cầu giấy phép là bắt buộc")
    @Column(name = "require_license", nullable = false)
    private Boolean requireLicense;

    @NotNull(message = "Cấp độ khóa học là bắt buộc")
    @Min(value = 1, message = "Cấp độ phải từ 1 trở lên")
    @Column(name = "level", nullable = false)
    private Integer level;

    @NotNull(message = "Tổng số bài học là bắt buộc")
    @Min(value = 1, message = "Khóa học phải có ít nhất 1 bài học")
    @Column(name = "total_lessons", nullable = false)
    private Integer totalLessons;

    @NotNull(message = "Tổng thời lượng là bắt buộc")
    @Min(value = 1, message = "Tổng thời lượng phải ít nhất 1 giây")
    @Column(name = "total_duration", nullable = false)
    private Integer totalDuration; // tính bằng giây

    @Size(max = 512, message = "Đường dẫn ảnh không được vượt quá 512 ký tự")
    @Column(name = "image_url", length = 512)
    private String imageUrl;

    @NotBlank(message = "Slug là bắt buộc")
    @Size(max = 255, message = "Slug không được vượt quá 255 ký tự")
    @Column(name = "slug", nullable = false, unique = true, length = 255)
    private String slug;

    @NotNull(message = "Danh mục là bắt buộc")
    @Column(name = "category_id", nullable = false, columnDefinition = "uuid")
    private UUID categoryId;
}


