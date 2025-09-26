package site.alphacode.alphacodecourseservice.dto.request.create;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCourse {
    @NotBlank(message = "Tên khóa học là bắt buộc")
    @Size(max = 255, message = "Tên khóa học không được vượt quá 255 ký tự")
    private String name;

    @NotBlank(message = "Mô tả khóa học là bắt buộc")
    private String description;

    @NotNull(message = "Giá khóa học là bắt buộc")
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá phải lớn hơn hoặc bằng 0")
    @Digits(integer = 8, fraction = 2, message = "Giá phải là số hợp lệ, tối đa 8 chữ số nguyên và 2 chữ số thập phân")
    private BigDecimal price;

    @NotNull(message = "Trạng thái yêu cầu giấy phép là bắt buộc")
    private Boolean requireLicense;

    @NotNull(message = "Cấp độ khóa học là bắt buộc")
    @Min(value = 1, message = "Cấp độ phải từ 1 trở lên")
    private Integer level;

    @NotNull(message = "Ảnh danh mục là bắt buộc")
    private MultipartFile image;

    @NotNull(message = "Danh mục là bắt buộc")
    private UUID categoryId;
}
