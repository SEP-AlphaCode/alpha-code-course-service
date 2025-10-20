package site.alphacode.alphacodecourseservice.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StaffDashboardStats {
    private long totalCategories;
    private long totalCourses;
    private long totalSections;
    private long totalLessons;
}

