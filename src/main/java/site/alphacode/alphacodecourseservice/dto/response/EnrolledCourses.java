package site.alphacode.alphacodecourseservice.dto.response;

import com.fasterxml.jackson.core.JsonToken;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class EnrolledCourses {
    private UUID id;
    private String name;
    private String imageUrl;
    private Integer progressPercent;
    private Integer completedLesson;
    private Integer totalLesson;
    private String lastAccessed;
    private String slug;
}
