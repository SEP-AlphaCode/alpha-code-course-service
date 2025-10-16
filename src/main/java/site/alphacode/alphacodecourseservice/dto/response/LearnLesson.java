package site.alphacode.alphacodecourseservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import site.alphacode.alphacodecourseservice.enums.LessonEnum;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class LearnLesson {
    private UUID id;
    private String title;
    private String content;
    private String videoUrl;
    private Integer duration;
    private Boolean requireRobot;
    private Integer type;

    @JsonProperty(value = "typeText")
    public String getTypeText() {
        return LessonEnum.fromCode(this.getType());
    }
}
