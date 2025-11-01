package site.alphacode.alphacodecourseservice.service;

import site.alphacode.alphacodecourseservice.dto.request.create.CreateCourseBundle;
import site.alphacode.alphacodecourseservice.dto.request.patch.PatchCourseBundle;
import site.alphacode.alphacodecourseservice.dto.request.update.UpdateCourseBundle;
import site.alphacode.alphacodecourseservice.dto.response.CourseBundleDto;
import site.alphacode.alphacodecourseservice.dto.response.CourseDto;

import java.util.List;
import java.util.UUID;

public interface CourseBundleService {
    List<CourseDto> courseBundle(UUID bundleId);
    CourseBundleDto update(UUID id, UpdateCourseBundle updateCourseBundle);
    CourseBundleDto patch(UUID id, PatchCourseBundle request);
    List<CourseBundleDto> create(CreateCourseBundle request);
    void delete(UUID id);
}
