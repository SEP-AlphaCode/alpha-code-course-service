package site.alphacode.alphacodecourseservice.dto.request.patch;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatchBundle {
    @Size(max = 255, message = "Tên không được vượt quá 255 ký tự")
    private String name;

    private String description;

    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    private BigDecimal price;

    @DecimalMin(value = "0.0", inclusive = true, message = "Giá giảm phải >= 0")
    private BigDecimal discountPrice;

    @Schema(description = "URL ảnh cũ, nếu giữ nguyên")
    private String coverImage;

    @Schema(type = "string", format = "binary", description = "File ảnh mới để upload", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private MultipartFile image;

    private Integer status;
}
