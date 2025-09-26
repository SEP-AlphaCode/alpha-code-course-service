package site.alphacode.alphacodecourseservice.mapper;

import site.alphacode.alphacodecourseservice.dto.response.CourseDto;
import site.alphacode.alphacodecourseservice.entity.Course;

public class CourseMapper {
    public static CourseDto toDto(Course course) {
        if (course == null) {
            return null;
        }
        CourseDto courseDto = new CourseDto();
        courseDto.setId(course.getId());
        courseDto.setName(course.getName());
        courseDto.setDescription(course.getDescription());
        courseDto.setLevel(course.getLevel());
        courseDto.setPrice(course.getPrice());
        courseDto.setSlug(course.getSlug());
        courseDto.setCategoryId(course.getCategoryId());
        courseDto.setCreatedDate(course.getCreatedDate());
        courseDto.setLastUpdated(course.getLastUpdated());
        courseDto.setImageUrl(course.getImageUrl());
        courseDto.setTotalLessons(course.getTotalLessons());
        courseDto.setTotalDuration(course.getTotalDuration());
        courseDto.setStatus(course.getStatus());
        courseDto.setRequireLicense(course.getRequireLicense());

        return courseDto;
    }
}
