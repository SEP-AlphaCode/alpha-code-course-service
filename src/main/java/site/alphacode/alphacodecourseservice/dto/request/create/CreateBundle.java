package site.alphacode.alphacodecourseservice.dto.request.create;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

import jakarta.validation.constraints.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateBundle {

    @NotBlank(message = "Tên bundle là bắt buộc")
    @Size(max = 255, message = "Tên không được vượt quá 255 ký tự")
    private String name;

    @NotBlank(message = "Mô tả là bắt buộc")
    private String description;

    @NotNull(message = "Giá là bắt buộc")
    @Min(value = 1, message = "Số tiền phải lớn hơn 0")
    private Integer price;

    @Min(value = 1, message = "Số tiền giảm phải lớn hơn 0")
    private Integer discountPrice;

    @NotNull(message = "Ảnh là bắt buộc")
    private MultipartFile coverImage;
}

