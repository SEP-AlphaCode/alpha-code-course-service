package site.alphacode.alphacodecourseservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LearningStats implements Serializable {
    private Integer totalCourses;
    private Integer completedCourses;
    private Integer inProgressCourses;
    private Integer totalLessonsCompleted;
    private Double learningHoursThisWeek;
}
