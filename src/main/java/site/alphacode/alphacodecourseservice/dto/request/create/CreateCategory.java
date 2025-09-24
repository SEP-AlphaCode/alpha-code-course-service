package site.alphacode.alphacodecourseservice.dto.request.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCategory {

    @NotBlank(message = "Tên danh mục là bắt buộc")
    @Size(max = 255, message = "Tên danh mục không được vượt quá 255 ký tự")
    private String name;

    @NotBlank(message = "Mô tả danh mục là bắt buộc")
    private String description;

    @NotNull(message = "Ảnh danh mục là bắt buộc")
    private MultipartFile image;
}

