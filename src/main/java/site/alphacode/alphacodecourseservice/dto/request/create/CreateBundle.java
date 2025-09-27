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
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    private BigDecimal price;

    @DecimalMin(value = "0.0", inclusive = true, message = "Giá giảm phải >= 0")
    private BigDecimal discountPrice;

    @NotNull(message = "Ảnh là bắt buộc")
    private MultipartFile coverImage;
}

