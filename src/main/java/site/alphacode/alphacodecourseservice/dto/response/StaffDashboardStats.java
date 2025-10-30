package site.alphacode.alphacodecourseservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class StaffDashboardStats implements Serializable {
    private long totalCategories;
    private long totalCourses;
    private long totalSections;
    private long totalLessons;
}

