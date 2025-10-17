package site.alphacode.alphacodecourseservice.dto.request.update;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateLesson {
        @NotNull(message = "Id bài học là bắt buộc")
        private UUID id;

        @NotBlank(message = "Tiêu đề bài học là bắt buộc")
        @Size(max = 255, message = "Tiêu đề không được dài quá 255 ký tự")
        private String title;

        @NotBlank(message = "Loại nội dung là bắt buộc")
        @Size(max = 100, message = "ContentType không được dài quá 100 ký tự")
        private String contentType;

        private String content; // text hoặc URL

        private MultipartFile videoFile; // nếu update video

        @NotNull(message = "Cờ requireRobot là bắt buộc")
        private Boolean requireRobot;

        @NotNull(message = "Giải pháp là bắt buộc")
        private JsonNode solution;


        @NotNull(message = "Trạng thái là bắt buộc")
        private Integer status;

        @NotNull(message = "SectionId là bắt buộc")
        private UUID sectionId;

        @NotNull(message = "Thời lượng bài học là bắt buộc")
        private Integer duration; // tính bằng giây

        @NotNull(message = "Loại bài học là bắt buộc")
        private Integer type;

        @NotNull(message = "Thứ tự bài học là bắt buộc")
        private Integer orderNumber;
}
