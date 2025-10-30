package site.alphacode.alphacodecourseservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import site.alphacode.alphacodecourseservice.enums.LessonStatusEnum;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class LearnLesson implements Serializable {
    private UUID id;
    private String slug;
    private String title;
    private String content;
    private String videoUrl;
    private Integer duration;
    private Boolean requireRobot;
    private Integer type;

    @JsonProperty(value = "typeText")
    public String getTypeText() {
        return LessonStatusEnum.fromCode(this.getType());
    }
}
