package site.alphacode.alphacodecourseservice.dto.request.create;

import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateLesson {

    @NotBlank(message = "Tiêu đề bài học là bắt buộc")
    @Size(max = 255, message = "Tiêu đề bài học không được vượt quá 255 ký tự")
    private String title;

    @NotBlank(message = "Loại nội dung là bắt buộc")
    @Size(max = 100, message = "Loại nội dung không được vượt quá 100 ký tự")
    private String contentType;

    private String content;

    @NotNull(message = "Thời lượng bài học là bắt buộc")
    @Min(value = 1, message = "Thời lượng bài học phải ít nhất 1 giây")
    private Integer duration;

    @NotNull(message = "Trạng thái yêu cầu robot là bắt buộc")
    private Boolean requireRobot;

    @NotNull(message = "Course ID là bắt buộc")
    private UUID courseId;

    @NotNull(message = "Dữ liệu giải pháp là bắt buộc")
    private JsonNode solution;
}
