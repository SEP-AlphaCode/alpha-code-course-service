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
@Table(name = "bundle")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Bundle extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @NotBlank(message = "Tên gói là bắt buộc")
    @Size(max = 255, message = "Tên gói không được vượt quá 255 ký tự")
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @NotBlank(message = "Mô tả gói là bắt buộc")
    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @NotNull(message = "Giá gói là bắt buộc")
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá phải lớn hơn hoặc bằng 0")
    @Digits(integer = 8, fraction = 2, message = "Giá không hợp lệ")
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
}

