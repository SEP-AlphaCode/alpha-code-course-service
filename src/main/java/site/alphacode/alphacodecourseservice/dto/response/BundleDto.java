package site.alphacode.alphacodecourseservice.dto.response;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.alphacode.alphacodecourseservice.base.BaseEntityDto;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BundleDto extends BaseEntityDto {
    private String id;

    @NotBlank(message = "Tên gói là bắt buộc")
    @Size(max = 255, message = "Tên gói không được vượt quá 255 ký tự")
    private String name;

    @NotBlank(message = "Mô tả gói là bắt buộc")
    private String description;

    @NotNull(message = "Giá gói là bắt buộc")
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá phải lớn hơn hoặc bằng 0")
    @Digits(integer = 8, fraction = 2, message = "Giá không hợp lệ")
    private BigDecimal price;
}

