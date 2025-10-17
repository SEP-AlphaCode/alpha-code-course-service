package site.alphacode.alphacodecourseservice.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import site.alphacode.alphacodecourseservice.base.BaseEntityDto;
import site.alphacode.alphacodecourseservice.enums.LessonStatusEnum;
import site.alphacode.alphacodecourseservice.enums.LessonTypeEnum;

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

    private String videoUrl;

    private Integer duration;

    private Boolean requireRobot;

    private Integer orderNumber;

    private UUID sectionId;

    private Integer type;

    @JsonProperty(value = "typeText")
    public String getTypeText() {
        return LessonTypeEnum.fromCode(this.getType());
    }

    @JsonProperty(value = "statusText")
    public String getStatusText() {
        return LessonStatusEnum.fromCode(this.getStatus());
    }
}

