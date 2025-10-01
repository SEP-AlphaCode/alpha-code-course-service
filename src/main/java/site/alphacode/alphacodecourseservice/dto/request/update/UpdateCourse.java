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
public class UpdateCourse {
    @NotNull(message = "Id khóa học là bắt buộc")
    private UUID id;

    @NotBlank(message = "Tên khóa học là bắt buộc")
    @Size(max = 255, message = "Tên khóa học không được vượt quá 255 ký tự")
    private String name;

    @NotBlank(message = "Mô tả khóa học là bắt buộc")
    private String description;

    @NotNull(message = "Giá khóa học là bắt buộc")
    @Min(value = 1, message = "Số tiền phải lớn hơn 0")
    private Integer price;

    @NotNull(message = "Trạng thái yêu cầu giấy phép là bắt buộc")
    private Boolean requireLicense;

    @NotNull(message = "Cấp độ khóa học là bắt buộc")
    @Min(value = 1, message = "Cấp độ phải từ 1 trở lên")
    private Integer level;

    @Schema(description = "URL ảnh cũ, nếu giữ nguyên")
    private String imageUrl;

    @Schema(type = "string", format = "binary", description = "File ảnh mới để upload", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private MultipartFile image;

    @NotNull(message = "Danh mục là bắt buộc")
    private UUID categoryId;

    @NotNull(message = "Trạng thái khóa học  là bắt buộc")
    private Integer status;
}
