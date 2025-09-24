package site.alphacode.alphacodecourseservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.alphacode.alphacodecourseservice.base.BaseEntityDto;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseDto extends BaseEntityDto {
    private String id;

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

    @NotNull(message = "Tổng số bài học là bắt buộc")
    @Min(value = 1, message = "Khóa học phải có ít nhất 1 bài học")
    private Integer totalLessons;

    @NotNull(message = "Tổng thời lượng là bắt buộc")
    @Min(value = 1, message = "Tổng thời lượng phải ít nhất 1 giây")
    private Integer totalDuration;

    @Size(max = 512, message = "Đường dẫn ảnh không được vượt quá 512 ký tự")
    private String imageUrl;

    @NotBlank(message = "Slug là bắt buộc")
    @Size(max = 255, message = "Slug không được vượt quá 255 ký tự")
    private String slug;

    @NotNull(message = "Danh mục là bắt buộc")
    private String categoryId;
}

