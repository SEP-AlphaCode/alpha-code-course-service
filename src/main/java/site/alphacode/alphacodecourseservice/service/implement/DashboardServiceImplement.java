package site.alphacode.alphacodecourseservice.service.implement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.alphacode.alphacodecourseservice.dto.response.StaffDashboardStats;
import site.alphacode.alphacodecourseservice.repository.CategoryRepository;
import site.alphacode.alphacodecourseservice.repository.CourseRepository;
import site.alphacode.alphacodecourseservice.repository.LessonRepository;
import site.alphacode.alphacodecourseservice.repository.SectionRepository;
import site.alphacode.alphacodecourseservice.service.DashboardService;

@Service
@RequiredArgsConstructor
public class DashboardServiceImplement implements DashboardService {

    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;
    private final LessonRepository lessonRepository;

    @Override
    public StaffDashboardStats getStaffDashboardStats() {
        long categories = categoryRepository.countNoneDeleted();
        long courses = courseRepository.countNoneDeleted();
        long sections = sectionRepository.countNoneDeleted();
        long lessons = lessonRepository.countNoneDeleted();

        return StaffDashboardStats.builder()
                .totalCategories(categories)
                .totalCourses(courses)
                .totalSections(sections)
                .totalLessons(lessons)
                .build();
    }
}

