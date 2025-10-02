package site.alphacode.alphacodecourseservice.dto.request.patch;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatchLesson {
    private String title;
    private String contentType;
    private String content; // text hoặc URL
    private MultipartFile videoFile; // nếu update video
    private Boolean requireRobot;
    private JsonNode solution;
    private Integer status;
    private Integer orderNumber;
    private Integer duration;
    private Integer type;
}
