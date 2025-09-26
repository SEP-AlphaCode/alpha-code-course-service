package site.alphacode.alphacodecourseservice.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import site.alphacode.alphacodecourseservice.base.BaseEntityDto;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LessonWithSolution extends BaseEntityDto implements Serializable {
    private UUID id;

    private String title;

    private String contentType;

    private String content;

    private Integer duration;

    private Boolean requireRobot;

    private Integer orderNumber;

    private UUID courseId;

    private JsonNode solution;
}
