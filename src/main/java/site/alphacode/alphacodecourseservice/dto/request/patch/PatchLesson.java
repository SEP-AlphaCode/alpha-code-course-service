package site.alphacode.alphacodecourseservice.dto.request.patch;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatchLesson {
    private String title;
    private String contentType;
    private String content;
    private Boolean requireRobot;
    private JsonNode solution;
    private Integer status;
    private Integer orderNumber;
    private Integer duration;
}
