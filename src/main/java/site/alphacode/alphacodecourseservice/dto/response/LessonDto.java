package site.alphacode.alphacodecourseservice.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import site.alphacode.alphacodecourseservice.base.BaseEntityDto;
import site.alphacode.alphacodecourseservice.enums.AccountLessonEnum;
import site.alphacode.alphacodecourseservice.enums.LessonEnum;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class LessonDto extends BaseEntityDto implements Serializable {
    private UUID id;

    private String title;

    private String content;

    private Integer duration;

    private Boolean requireRobot;

    private Integer orderNumber;

    private UUID courseId;

    private Integer type;

    @JsonProperty(value = "typeText")
    public String getTypeText() {
        return LessonEnum.fromCode(this.getType());
    }

    @JsonProperty(value = "statusText")
    public String getStatusText() {
        return LessonEnum.fromCode(this.getStatus());
    }
}

