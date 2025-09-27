package site.alphacode.alphacodecourseservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class LearnLesson {
    private UUID id;
    private String title;
    private String content;
    private String contentType;
    private Integer duration;
    private Boolean requireRobot;
}
