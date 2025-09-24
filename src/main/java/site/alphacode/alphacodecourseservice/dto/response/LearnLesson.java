package site.alphacode.alphacodecourseservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LearnLesson {
    private UUID id;
    private String title;
    private String content;
    private String contentUrl;
    private String contentType;
    private Integer duration;
    private Boolean requireRobot;
}
