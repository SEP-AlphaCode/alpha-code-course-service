package site.alphacode.alphacodecourseservice.dto.request.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import site.alphacode.alphacodecourseservice.validation.OnPut;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCategory {

    @NotBlank(message = "Tên danh mục là bắt buộc", groups = {OnPut.class})
    @Size(max = 255, message = "Tên danh mục không được vượt quá 255 ký tự", groups = {OnPut.class})
    private String name;

    @NotBlank(message = "Mô tả danh mục là bắt buộc", groups = {OnPut.class})
    private String description;

    // Nếu người dùng muốn giữ ảnh cũ thì truyền URL
    private String imageUrl;

    // Nếu muốn upload ảnh mới thì truyền file
    private MultipartFile image;

    @NotNull(message = "Trạng thái danh mục là bắt buộc", groups = {OnPut.class})
    private Integer status;
}


