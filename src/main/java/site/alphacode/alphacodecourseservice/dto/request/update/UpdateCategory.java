package site.alphacode.alphacodecourseservice.dto.request.update;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCategory {
    @NotNull(message = "Id danh mục là bắt buộc")
    private UUID id;

    @NotBlank(message = "Tên danh mục là bắt buộc")
    @Size(max = 255, message = "Tên danh mục không được vượt quá 255 ký tự")
    private String name;

    @NotBlank(message = "Mô tả danh mục là bắt buộc")
    private String description;

    @Schema(description = "URL ảnh cũ, nếu giữ nguyên")
    private String imageUrl;

    @Schema(type = "string", format = "binary", description = "File ảnh mới để upload", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private MultipartFile image;

    @NotNull(message = "Trạng thái danh mục là bắt buộc")
    private Integer status;
}


