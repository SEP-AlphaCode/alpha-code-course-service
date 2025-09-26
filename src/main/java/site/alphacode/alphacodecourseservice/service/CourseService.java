package site.alphacode.alphacodecourseservice.service;

import site.alphacode.alphacodecourseservice.dto.CourseDto;
import site.alphacode.alphacodecourseservice.dto.PagedResult;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateCourse;
import site.alphacode.alphacodecourseservice.dto.request.patch.PatchCourse;
import site.alphacode.alphacodecourseservice.dto.request.update.UpdateCourse;

import java.util.UUID;

public interface CourseService {
    CourseDto create(CreateCourse createCourse);
    CourseDto getActiveCourseById(UUID id);
    CourseDto getActiveCourseBySlug(String slug);
    PagedResult<CourseDto> getAllActiveCourses(int page, int size, String search);
    CourseDto update(UUID id, UpdateCourse updateCourse);
    CourseDto patchUpdate(UUID id, PatchCourse patchCourse);
    void delete(UUID id);
}
