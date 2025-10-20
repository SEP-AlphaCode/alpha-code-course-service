package site.alphacode.alphacodecourseservice.service.implement;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import site.alphacode.alphacodecourseservice.dto.response.CourseDto;
import site.alphacode.alphacodecourseservice.dto.response.PagedResult;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateCourse;
import site.alphacode.alphacodecourseservice.dto.request.patch.PatchCourse;
import site.alphacode.alphacodecourseservice.dto.request.update.UpdateCourse;
import site.alphacode.alphacodecourseservice.entity.Course;
import site.alphacode.alphacodecourseservice.exception.BadRequestException;
import site.alphacode.alphacodecourseservice.exception.ConflictException;
import site.alphacode.alphacodecourseservice.exception.ResourceNotFoundException;
import site.alphacode.alphacodecourseservice.mapper.CourseMapper;
import site.alphacode.alphacodecourseservice.repository.CategoryRepository;
import site.alphacode.alphacodecourseservice.repository.CourseRepository;
import site.alphacode.alphacodecourseservice.repository.SectionRepository;
import site.alphacode.alphacodecourseservice.service.CourseService;
import site.alphacode.alphacodecourseservice.service.S3Service;
import site.alphacode.alphacodecourseservice.util.SlugHelper;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseServiceImplement implements CourseService {
    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final S3Service s3Service;
    private final SectionRepository sectionRepository;

    @Override
    @Cacheable(value = "course", key = "{#id}")
    public CourseDto getActiveCourseById(UUID id) {
        var entity = courseRepository.findActiveCourseById(id);
        if(entity.isEmpty()) {
            throw new ResourceNotFoundException("Khóa học với id " + id + " không tồn tại.");
        }
        return CourseMapper.toDto(entity.get());
    }

    @Override
    @Cacheable(value = "course", key = "{#slug}")
    public CourseDto getCourseBySlug(String slug) {
        var entity = courseRepository.findCourseBySlug(slug);
        if(entity.isEmpty()) {
            throw new ResourceNotFoundException("Khóa học với slug " + slug + " không tồn tại.");
        }
        return CourseMapper.toDto(entity.get());
    }

    @Override
    @Cacheable(value = "none_delete_course", key = "{#id}")
    public CourseDto getNoneDeleteCourseById(UUID id) {
        var entity = courseRepository.findNoneDeleteCourseById(id);
        if(entity.isEmpty()) {
            throw new ResourceNotFoundException("Khóa học với id " + id + " không tồn tại.");
        }
        return CourseMapper.toDto(entity.get());
    }

    @Override
    @Cacheable(value = "courses_list", key = "{#page, #size, #search, #categoryId}")
    public PagedResult<CourseDto> getAllActiveCourses(int page, int size, String search, UUID categoryId) {
        var pageable = org.springframework.data.domain.PageRequest.of(page - 1, size);
        Page<Course> course = courseRepository.findAllActiveCourse(search, categoryId, pageable);
        Page<CourseDto> dtoPage = course.map(c -> {
            CourseDto dto = CourseMapper.toDto(c);
            int sectionCount = (int) sectionRepository.countActiveByCourseId(c.getId());
            dto.setSectionCount(sectionCount);
            return dto;
        });
        return new PagedResult<>(dtoPage);
    }

    @Override
    @Cacheable(value = "all_courses_list", key = "{#page, #size, #search}")
    public PagedResult<CourseDto> getNoneDeleteCourses(int page, int size, String search) {
        var pageable = org.springframework.data.domain.PageRequest.of(page - 1, size);
        Page<Course> course = courseRepository.findNoneDeleteCourses(search, pageable);
        Page<CourseDto> dtoPage = course.map(c -> {
            CourseDto dto = CourseMapper.toDto(c);
            int sectionCount = (int) sectionRepository.countActiveByCourseId(c.getId());
            dto.setSectionCount(sectionCount);
            return dto;
        });
        return new PagedResult<>(dtoPage);
    }

    @Override
    @Transactional
    @CachePut(value = "course", key = "{#result.id}") // thêm mới thì put vào cache theo id
    @Caching(
            evict = {
            @CacheEvict(value = "none_delete_course", allEntries = true),
            @CacheEvict(value = "all_courses_list", allEntries = true),
            @CacheEvict(value = "courses_list", allEntries = true)
    })
    public CourseDto create(CreateCourse createCourse) {
        if (courseRepository.existsByName(createCourse.getName())) {
            throw new ResourceNotFoundException("Khóa học với tên " + createCourse.getName() + " đã tồn tại.");
        }

        if(categoryRepository.findNoneDeleteCategoryById(createCourse.getCategoryId()).isEmpty()) {
            throw new ResourceNotFoundException("Danh mục với id " + createCourse.getCategoryId() + " không tồn tại.");
        }

        Course course = new Course();
        course.setCategoryId(createCourse.getCategoryId());
        course.setDescription(createCourse.getDescription());
        course.setLevel(createCourse.getLevel());
        course.setName(createCourse.getName());
        course.setPrice(createCourse.getPrice());
        course.setRequireLicense(createCourse.getRequireLicense());
        course.setSlug(SlugHelper.toSlug(createCourse.getName()));
        course.setTotalDuration(0);
        course.setTotalLessons(0);
        course.setCreatedDate(LocalDateTime.now());
        course.setLastUpdated(null);
        course.setStatus(2); // Mặc định tạo mới là Inactive
        try {
            if (createCourse.getImage() != null && !createCourse.getImage().isEmpty()) {
                String fileKey = "courses/" + System.currentTimeMillis() + "_" +  createCourse.getImage().getOriginalFilename();
                String imageUrl = s3Service.uploadBytes(createCourse.getImage().getBytes(), fileKey, createCourse.getImage().getContentType());
                course.setImageUrl(imageUrl);
            }

            Course savedEntity = courseRepository.save(course);
            return CourseMapper.toDto(savedEntity);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo Category", e);
        }
    }

    @Override
    @Transactional
    @CachePut(value = "course", key = "{#id}")
    @Caching(evict = {
            @CacheEvict(value = "none_delete_course", allEntries = true),
            @CacheEvict(value = "all_courses_list", allEntries = true),
            @CacheEvict(value = "courses_list", allEntries = true)
    })
    public CourseDto update(UUID id, UpdateCourse updateCourse) {
        var existing = courseRepository.findNoneDeleteCourseById(id);
        if(existing.isEmpty()) {
            throw new ResourceNotFoundException("Khóa học với id " + id + " không tồn tại.");
        }

        if (courseRepository.existsByName(updateCourse.getName())) {
            throw new ConflictException("Khóa học với tên " + updateCourse.getName() + " đã tồn tại.");
        }

        if(categoryRepository.findNoneDeleteCategoryById(updateCourse.getCategoryId()).isEmpty()) {
            throw new ResourceNotFoundException("Danh mục với id " + updateCourse.getCategoryId() + " không tồn tại.");
        }

        existing.get().setCategoryId(updateCourse.getCategoryId());
        existing.get().setDescription(updateCourse.getDescription());
        existing.get().setLevel(updateCourse.getLevel());
        existing.get().setName(updateCourse.getName());
        existing.get().setPrice(updateCourse.getPrice());
        existing.get().setRequireLicense(updateCourse.getRequireLicense());
        existing.get().setSlug(SlugHelper.toSlug(updateCourse.getName()));
        existing.get().setLastUpdated(LocalDateTime.now());
        if (updateCourse.getStatus() != null && updateCourse.getStatus() == 1) {
            if (existing.get().getTotalLessons() <= 0) {
                throw new BadRequestException("Không thể active khóa học vì chưa có lesson nào.");
            }
        }




        // Xử lý ảnh (PUT => bắt buộc có image file hoặc imageUrl)
        if (updateCourse.getImage() != null && !updateCourse.getImage().isEmpty()) {
            try {
                String fileKey = "courses/" + System.currentTimeMillis() + "_" +  updateCourse.getImage().getOriginalFilename();
                String imageUrl = s3Service.uploadBytes(
                        updateCourse.getImage().getBytes(),
                        fileKey,
                        updateCourse.getImage().getContentType()
                );
                existing.get().setImageUrl(imageUrl);
            } catch (Exception e) {
                throw new RuntimeException("Lỗi khi tải lên ảnh", e);
            }
        } else if (updateCourse.getImageUrl() != null && !updateCourse.getImageUrl().isBlank()) {
            existing.get().setImageUrl(updateCourse.getImageUrl());
        } else {
            throw new BadRequestException("Chỉnh sửa gói phải có ảnh (image file hoặc imageUrl)");
        }

        Course savedEntity = courseRepository.save(existing.get());
        return CourseMapper.toDto(savedEntity);
    }

    @Override
    @Transactional
    @CachePut(value = "course", key = "{#id}")
    @Caching(evict = {
            @CacheEvict(value = "none_delete_course", allEntries = true),
            @CacheEvict(value = "all_courses_list", allEntries = true),
            @CacheEvict(value = "courses_list", allEntries = true)
    })
    public CourseDto patchUpdate(UUID id, PatchCourse patchCourse) {
        var existing = courseRepository.findNoneDeleteCourseById(id);
        if (existing.isEmpty()) {
            throw new ResourceNotFoundException("Khóa học với id " + id + " không tồn tại.");
        }

        if (patchCourse.getName() != null && !patchCourse.getName().isBlank()
                && !patchCourse.getName().equals(existing.get().getName())
                && courseRepository.existsByName(patchCourse.getName())) {
            throw new ConflictException("Khóa học với tên " + patchCourse.getName() + " đã tồn tại.");
        }

        if (patchCourse.getCategoryId() != null
                && categoryRepository.findNoneDeleteCategoryById(patchCourse.getCategoryId()).isEmpty()) {
            throw new ResourceNotFoundException("Danh mục với id " + patchCourse.getCategoryId() + " không tồn tại.");
        }

        if (patchCourse.getCategoryId() != null) {
            existing.get().setCategoryId(patchCourse.getCategoryId());
        }
        if (patchCourse.getDescription() != null) {
            existing.get().setDescription(patchCourse.getDescription());
        }
        if (patchCourse.getLevel() != null) {
            existing.get().setLevel(patchCourse.getLevel());
        }
        if (patchCourse.getName() != null && !patchCourse.getName().isBlank()) {
            existing.get().setName(patchCourse.getName());
            existing.get().setSlug(SlugHelper.toSlug(patchCourse.getName()));
        }
        if (patchCourse.getPrice() != null) {
            existing.get().setPrice(patchCourse.getPrice());
        }
        if (patchCourse.getRequireLicense() != null) {
            existing.get().setRequireLicense(patchCourse.getRequireLicense());
        }
        if (patchCourse.getStatus() != null && patchCourse.getStatus() == 1) {
            if (existing.get().getTotalLessons() <= 0) {
                throw new BadRequestException("Không thể active khóa học vì chưa có lesson nào.");
            }
            existing.get().setStatus(patchCourse.getStatus());
        }
        existing.get().setLastUpdated(LocalDateTime.now());

        // Xử lý ảnh (PATCH => có thể có hoặc không)
        if (patchCourse.getImage() != null && !patchCourse.getImage().isEmpty()) {
            try {
                String fileKey = "courses/" + System.currentTimeMillis() + "_" +  patchCourse.getImage().getOriginalFilename();
                String imageUrl = s3Service.uploadBytes(
                        patchCourse.getImage().getBytes(),
                        fileKey,
                        patchCourse.getImage().getContentType()
                );
                existing.get().setImageUrl(imageUrl);
            } catch (Exception e) {
                throw new RuntimeException("Lỗi khi tải lên ảnh", e);
            }
        } else if (patchCourse.getImageUrl() != null && !patchCourse.getImageUrl().isBlank()) {
            existing.get().setImageUrl(patchCourse.getImageUrl());
        }

        Course savedEntity = courseRepository.save(existing.get());
        return CourseMapper.toDto(savedEntity);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "course", key = "{#id}"),
            @CacheEvict(value = "none_delete_course", allEntries = true),
            @CacheEvict(value = "all_courses_list", allEntries = true),
            @CacheEvict(value = "courses_list", allEntries = true)
    })
    public void delete(UUID id) {
        var existing = courseRepository.findNoneDeleteCourseById(id);
        if(existing.isEmpty()) {
            throw new ResourceNotFoundException("Khóa học với id " + id + " không tồn tại.");
        }
        existing.get().setStatus(0); // set trạng thái xóa mềm
        existing.get().setLastUpdated(LocalDateTime.now());
        courseRepository.save(existing.get());
    }

}
