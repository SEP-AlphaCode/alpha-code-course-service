package site.alphacode.alphacodecourseservice.dto.request.patch;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatchLesson {
    private String title;
    private String content; // text hoặc URL
    private Boolean requireRobot;
    private JsonNode solution;
    private Integer status;
    private Integer orderNumber;
    private Integer duration;
    private Integer type;
    private UUID sectionId;

    private String videoUrl; // optional: FE direct upload URL
}
