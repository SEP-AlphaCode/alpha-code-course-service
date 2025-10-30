package site.alphacode.alphacodecourseservice.dto.request.create;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateLesson {

    @NotBlank(message = "Tiêu đề bài học là bắt buộc")
    @Size(max = 255, message = "Tiêu đề bài học không được vượt quá 255 ký tự")
    private String title;

    private String content;

    @NotNull(message = "Thời lượng bài học là bắt buộc")
    @Min(value = 1, message = "Thời lượng bài học phải ít nhất 1 giây")
    private Integer duration;

    @NotNull(message = "Trạng thái yêu cầu robot là bắt buộc")
    private Boolean requireRobot;

    @NotNull(message = "Section ID là bắt buộc")
    private UUID sectionId;

    @NotNull(message = "Loại bài học là bắt buộc")
    private Integer type;

    private JsonNode solution;

    private String videoUrl; // optional: FE direct upload URL
}
