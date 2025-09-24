package site.alphacode.alphacodecourseservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.alphacode.alphacodecourseservice.base.BaseEntityDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LessonDto extends BaseEntityDto {
    private String id;

    @NotBlank(message = "Tiêu đề bài học là bắt buộc")
    @Size(max = 255, message = "Tiêu đề bài học không được vượt quá 255 ký tự")
    private String title;

    @Size(max = 500, message = "Đường dẫn nội dung không được vượt quá 500 ký tự")
    private String contentUrl;

    @NotBlank(message = "Loại nội dung là bắt buộc")
    @Size(max = 100, message = "Loại nội dung không được vượt quá 100 ký tự")
    private String contentType;

    private String content;

    @NotNull(message = "Thời lượng bài học là bắt buộc")
    @Min(value = 1, message = "Thời lượng bài học phải ít nhất 1 giây")
    private Integer duration;

    @NotNull(message = "Trạng thái yêu cầu robot là bắt buộc")
    private Boolean requireRobot = false;

    @NotNull(message = "Course ID là bắt buộc")
    private String courseId;
}

