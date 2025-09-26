package site.alphacode.alphacodecourseservice.dto.request.patch;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class PatchCourse {
    @Size(max = 255, message = "Tên khóa học không được vượt quá 255 ký tự")
    private String name;

    private String description;

    @DecimalMin(value = "0.0", inclusive = true, message = "Giá phải lớn hơn hoặc bằng 0")
    @Digits(integer = 8, fraction = 2, message = "Giá phải là số hợp lệ, tối đa 8 chữ số nguyên và 2 chữ số thập phân")
    private BigDecimal price;

    private Boolean requireLicense;

    @Min(value = 1, message = "Cấp độ phải từ 1 trở lên")
    private Integer level;

    @Schema(description = "URL ảnh cũ, nếu giữ nguyên")
    private String imageUrl;

    @Schema(type = "string", format = "binary", description = "File ảnh mới để upload", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private MultipartFile image;

    private UUID categoryId;

    private Integer status;
}
