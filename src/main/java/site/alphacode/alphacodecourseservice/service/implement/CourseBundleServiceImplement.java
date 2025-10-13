package site.alphacode.alphacodecourseservice.service.implement;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateCourseBundle;
import site.alphacode.alphacodecourseservice.dto.request.patch.PatchCourseBundle;
import site.alphacode.alphacodecourseservice.dto.request.update.UpdateCourseBundle;
import site.alphacode.alphacodecourseservice.dto.response.CourseBundleDto;
import site.alphacode.alphacodecourseservice.dto.response.CourseDto;
import site.alphacode.alphacodecourseservice.entity.Course;
import site.alphacode.alphacodecourseservice.entity.CourseBundle;
import site.alphacode.alphacodecourseservice.exception.ConflictException;
import site.alphacode.alphacodecourseservice.exception.ResourceNotFoundException;
import site.alphacode.alphacodecourseservice.mapper.CourseBundleMapper;
import site.alphacode.alphacodecourseservice.mapper.CourseMapper;
import site.alphacode.alphacodecourseservice.repository.BundleRepository;
import site.alphacode.alphacodecourseservice.repository.CourseBundleRepository;
import site.alphacode.alphacodecourseservice.repository.CourseRepository;
import site.alphacode.alphacodecourseservice.service.CourseBundleService;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseBundleServiceImplement implements CourseBundleService {
    private final CourseBundleRepository courseBundleRepository;
    private final CourseRepository courseRepository;
    private final BundleRepository bundleRepository;

    @Override
    @Cacheable(value = "courses_in_bundle", key = "#bundleId")
    public List<CourseDto> courseBundle(UUID bundleId) {
        if (!bundleRepository.existsById(bundleId)) {
            throw new ResourceNotFoundException("Không tìm thấy bundle: " + bundleId);
        }

        // Lấy danh sách courseId từ bảng trung gian
        List<UUID> courseIds = courseBundleRepository.findCourseIdsByBundleId(bundleId);

        // Lấy danh sách Course thực tế
        List<Course> courses = courseRepository.findAllById(courseIds);

        return courses.stream()
                .map(CourseMapper::toDto)
                .toList();
    }



    @Override
    @Transactional
    @CachePut(value = "course_bundle", key = "#result.id")
    @CacheEvict(value = "courses_in_bundle", key = "#p0.bundleId")
    public CourseBundleDto create(CreateCourseBundle request) {
        log.info("Create CourseBundle request: courseId={}, bundleId={}", request.getCourseId(), request.getBundleId());
        // 1. Kiểm tra các course có tồn tại không
        var course = courseRepository.findActiveCourseById(request.getCourseId()).orElseThrow(
                () -> new ResourceNotFoundException("Không tìm thấy courseId với id: " + request.getCourseId())
        );

        var bundle = bundleRepository.findNoneDeleteById(request.getBundleId()).orElseThrow(
                () -> new ResourceNotFoundException("Không tìm thấy bundle với id: " + request.getBundleId())
        );

        var courseBundleExists = courseBundleRepository.existsByCourseIdAndBundleId(request.getCourseId(), request.getBundleId());
        if (courseBundleExists) {
            throw new ConflictException("CourseBundle đã tồn tại với courseId: " + request.getCourseId() + " và bundleId: " + request.getBundleId());
        }

        CourseBundle courseBundle = new CourseBundle();
        courseBundle.setBundleId(bundle.getId());
        courseBundle.setCourseId(course.getId());
        courseBundle.setStatus(1);
        courseBundle.setCreatedDate(LocalDateTime.now());
        courseBundle.setLastUpdated(null);

        // 2. Tạo course bundle
        var savedCourseBundle = courseBundleRepository.save(courseBundle);
        log.debug("Saved CourseBundle id={} (bundleId={}, courseId={})", savedCourseBundle.getId(), savedCourseBundle.getBundleId(), savedCourseBundle.getCourseId());

        return CourseBundleMapper.toDto(savedCourseBundle);
    }

    @Override
    @Cacheable(value = "course_bundle", key = "{#id}")
    @CacheEvict(value = "courses_in_bundle", key = "{#result.bundleId}")
    @Transactional
    public CourseBundleDto update(UUID id,UpdateCourseBundle updateCourseBundle){
        var existingCourseBundle = courseBundleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy CourseBundle với id: " + updateCourseBundle.getId()));

        // Cập nhật các trường cần thiết
        var course = courseRepository.findActiveCourseById(updateCourseBundle.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy course với id: " + updateCourseBundle.getCourseId()));

        var bundle = bundleRepository.findNoneDeleteById(updateCourseBundle.getBundleId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bundle với id: " + updateCourseBundle.getBundleId()));

        existingCourseBundle.setCourseId(course.getId());
        existingCourseBundle.setBundleId(bundle.getId());
        existingCourseBundle.setStatus(updateCourseBundle.getStatus());
        existingCourseBundle.setLastUpdated(LocalDateTime.now());

        // Lưu thay đổi
        var updatedCourseBundle = courseBundleRepository.save(existingCourseBundle);

        return CourseBundleMapper.toDto(updatedCourseBundle);
    }

    @Override
    @CachePut(value = "course_bundle", key = "{#id}")
    @CacheEvict(value = "courses_in_bundle", key = "{#result.bundleId}")
    @Transactional
    public CourseBundleDto patch(UUID id, PatchCourseBundle request) {
        var existingCourseBundle = courseBundleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy CourseBundle với id: " + id));

        if (request.getCourseId() != null) {
            var course = courseRepository.findActiveCourseById(request.getCourseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy course với id: " + request.getCourseId()));
            existingCourseBundle.setCourseId(course.getId());
        }

        if (request.getBundleId() != null) {
            var bundle = bundleRepository.findNoneDeleteById(request.getBundleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bundle với id: " + request.getBundleId()));
            existingCourseBundle.setBundleId(bundle.getId());
        }

        if (request.getStatus() != null) {
            existingCourseBundle.setStatus(request.getStatus());
        }

        existingCourseBundle.setLastUpdated(LocalDateTime.now());

        var updatedCourseBundle = courseBundleRepository.save(existingCourseBundle);
        return CourseBundleMapper.toDto(updatedCourseBundle);
    }

    @Override
    @CacheEvict(value = {"course_bundle", "courses_in_bundle"}, allEntries = true)
    @Transactional
    public void delete(UUID id) {
        var existingCourseBundle = courseBundleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy CourseBundle với id: " + id));

        existingCourseBundle.setStatus(0);
        courseBundleRepository.save(existingCourseBundle);
    }
}
