package site.alphacode.alphacodecourseservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AvailableCourse {
    private UUID id;
    private String name;
    private String imageUrl;
    private Integer totalLesson;
    private String slug;
    private Integer price;
    private String description;
}
