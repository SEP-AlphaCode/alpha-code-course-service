package site.alphacode.alphacodecourseservice.dto.request.update;

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
public class UpdateBundle {
    @NotNull(message = "Id gói là bắt buộc")
    private UUID id;

    @NotBlank(message = "Tên bundle là bắt buộc")
    @Size(max = 255, message = "Tên không được vượt quá 255 ký tự")
    private String name;

    @NotBlank(message = "Mô tả là bắt buộc")
    private String description;

    @NotNull(message = "Giá là bắt buộc")
    @Min(value = 1, message = "Số tiền phải lớn hơn 0")
    private Integer price;

    @NotNull(message = "Giá giảm là bắt buộc")
    @Min(value = 1, message = "Số tiền giảm phải lớn hơn 0")
    private Integer discountPrice;

    @Schema(description = "URL ảnh cũ, nếu giữ nguyên")
    private String coverImage;

    @Schema(type = "string", format = "binary", description = "File ảnh mới để upload", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private MultipartFile image;

    @NotNull(message = "Trạng thái gói là bắt buộc")
    private Integer status;
}
