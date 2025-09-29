package site.alphacode.alphacodecourseservice.mapper;

import site.alphacode.alphacodecourseservice.dto.response.CourseBundleDto;
import site.alphacode.alphacodecourseservice.entity.CourseBundle;

public class CourseBundleMapper {
    public static CourseBundleDto toDto(CourseBundle courseBundle) {
        if (courseBundle == null) {
            return null;
        }
        return CourseBundleDto.builder()
                .id(courseBundle.getId())
                .bundleId(courseBundle.getBundleId())
                .courseId(courseBundle.getCourseId())
                .status(courseBundle.getStatus())
                .createdDate(courseBundle.getCreatedDate())
                .lastUpdated(courseBundle.getLastUpdated())
                .build();
    }
}
