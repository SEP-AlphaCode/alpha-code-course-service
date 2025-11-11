package site.alphacode.alphacodecourseservice.service.implement;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.alphacode.alphacodecourseservice.dto.response.*;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateAccountCourse;
import site.alphacode.alphacodecourseservice.entity.AccountCourse;
import site.alphacode.alphacodecourseservice.entity.Course;
import site.alphacode.alphacodecourseservice.exception.ConflictException;
import site.alphacode.alphacodecourseservice.mapper.AccountCourseMapper;
import site.alphacode.alphacodecourseservice.repository.AccountCourseRepository;
import site.alphacode.alphacodecourseservice.repository.AccountLessonRepository;
import site.alphacode.alphacodecourseservice.repository.CourseBundleRepository;
import site.alphacode.alphacodecourseservice.repository.CourseRepository;
import site.alphacode.alphacodecourseservice.repository.LessonRepository;
import site.alphacode.alphacodecourseservice.service.AccountCourseService;
import site.alphacode.alphacodecourseservice.service.CourseService;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountCourseServiceImplement implements AccountCourseService {
    private final AccountCourseRepository repository;
    private final AccountLessonRepository accountLessonRepository;
    private final LessonRepository lessonRepository;
    private final CourseBundleRepository courseBundleRepository;
    private final CourseService courseService;
    private final CourseRepository courseRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String LEARNING_HOURS_KEY_PREFIX = "learning:hours:";

    @Override
    @Transactional
    @CachePut(value = "account_course", key = "{#accountCourseId}")
    public AccountCourseDto getAccountCourseById(UUID accountCourseId) {
        // update last accessed
        repository.updateLastAccessed(accountCourseId, LocalDateTime.now());
        var accountCourse = repository.findById(accountCourseId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy AccountCourse với id: " + accountCourseId));
        return AccountCourseMapper.toDto(accountCourse);
    }

    @Override
    @Transactional
    @CachePut(value = "account_course", key = "{#result.id}")
    @CacheEvict(value = {"account_courses", "account_course", "available_courses", "enrolled_courses"}, allEntries = true)
    public AccountCourseDto create(CreateAccountCourse createAccountCourse) {
        if(repository.existsByAccountIdAndCourseId(createAccountCourse.getAccountId(), createAccountCourse.getCourseId())) {
            throw new ConflictException("Khóa học đã được mua trước đó");
        }

        var course = courseService.getNoneDeleteCourseById(createAccountCourse.getCourseId());
        if(course.getPrice() > 0) {
            throw new ConflictException("Khóa học trả phí không thể được tạo tự động");

        }

        AccountCourse accountCourse = new AccountCourse();
        accountCourse.setAccountId(createAccountCourse.getAccountId());
        accountCourse.setCourseId(createAccountCourse.getCourseId());
        accountCourse.setStatus(1);
        accountCourse.setPurchaseDate(LocalDateTime.now());
        accountCourse.setLastAccessed(null);
        accountCourse.setCompletedLesson(0);
        accountCourse.setCompleted(false);
        accountCourse.setProgressPercent(0);
        accountCourse.setTotalLesson(lessonRepository.countActiveLessonsByCourseId(createAccountCourse.getCourseId()));

        accountCourse = repository.save(accountCourse);
        return AccountCourseMapper.toDto(accountCourse);
    }

    @Override
    @Transactional
    @CachePut(value = "account_course", key = "{#result.id}")
    @CacheEvict(value = {"account_courses", "account_course", "available_courses", "enrolled_courses"}, allEntries = true)
    public void createFromPayment(CreateAccountCourse createAccountCourse) {
        if(repository.existsByAccountIdAndCourseId(createAccountCourse.getAccountId(), createAccountCourse.getCourseId())) {
            throw new ConflictException("Khóa học đã được mua trước đó");
        }

        AccountCourse accountCourse = new AccountCourse();
        accountCourse.setAccountId(createAccountCourse.getAccountId());
        accountCourse.setCourseId(createAccountCourse.getCourseId());
        accountCourse.setStatus(1);
        accountCourse.setPurchaseDate(LocalDateTime.now());
        accountCourse.setLastAccessed(null);
        accountCourse.setCompletedLesson(0);
        accountCourse.setCompleted(false);
        accountCourse.setProgressPercent(0);
        accountCourse.setTotalLesson(lessonRepository.countActiveLessonsByCourseId(createAccountCourse.getCourseId()));

        accountCourse = repository.save(accountCourse);
        AccountCourseMapper.toDto(accountCourse);
    }

    @Override
    @Cacheable(value = "account_courses", key = "{#accountId, #page, #size}")
    public PagedResult<AccountCourseDto> getAccountCoursesByAccountId(UUID accountId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<AccountCourse> pageResult = repository.findActiveByAccountId(accountId, pageable);
        return new PagedResult<>(pageResult.map(AccountCourseMapper::toDto));
    }

    @Override
    @Transactional
    @CacheEvict(value = {"account_courses", "account_course", "available_courses", "enrolled_courses", "account_lessons", "account_lesson_with_lesson", "account_lesson_by_account_course"}, allEntries = true)
    public void delete(UUID id) {
        var accountCourse = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy AccountCourse với id: " + id));

        // Soft delete the AccountCourse
        repository.softDeleteById(accountCourse.getId());

        // Soft delete all associated AccountLessons
        accountLessonRepository.softDeleteByAccountIdAndCourseId(accountCourse.getAccountId(), accountCourse.getCourseId());

        log.info("Soft deleted AccountCourse id: {} and all associated AccountLessons for accountId: {} and courseId: {}",
                 id, accountCourse.getAccountId(), accountCourse.getCourseId());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"account_courses", "account_course", "available_courses", "enrolled_courses"}, allEntries = true)
    public List<AccountCourseDto> createFromBundle(UUID accountId, UUID bundleId) {
        // Giả sử bạn có repository CourseBundleRepository để lấy danh sách course trong bundle
        List<UUID> courseIdsInBundle = courseBundleRepository.findCourseIdsByBundleId(bundleId);

        List<AccountCourseDto> createdCourses = new ArrayList<>();

        for (UUID courseId : courseIdsInBundle) {
            // Bỏ qua khóa học đã có
            if (repository.existsByAccountIdAndCourseId(accountId, courseId)) {
                continue;
            }

            AccountCourse accountCourse = new AccountCourse();
            accountCourse.setAccountId(accountId);
            accountCourse.setCourseId(courseId);
            accountCourse.setStatus(1);
            accountCourse.setPurchaseDate(LocalDateTime.now());
            accountCourse.setLastAccessed(null);
            accountCourse.setCompletedLesson(0);
            accountCourse.setCompleted(false);
            accountCourse.setProgressPercent(0);
            accountCourse.setTotalLesson(lessonRepository.countActiveLessonsByCourseId(courseId));

            accountCourse = repository.save(accountCourse);
            createdCourses.add(AccountCourseMapper.toDto(accountCourse));
        }

        return createdCourses;
    }

    @Override
    @Cacheable(value = "account_course", key = "{#accountId, #courseId}")
    public AccountCourseDto getByAccountIdAndCourseId(UUID accountId, UUID courseId) {
        var accountCourse = repository.findByAccountIdAndCourseId(accountId, courseId)
                .orElseThrow(() -> new EntityNotFoundException("Tài khoản chưa đăng ký khóa học này."));
        return AccountCourseMapper.toDto(accountCourse);
    }

    @Override
    @Cacheable(value = "enrolled_courses", key = "{#accountId, #size}")
    public List<EnrolledCourses> getEnrolledCourses(UUID accountId, Integer size) {
        // Lấy danh sách AccountCourse mới nhất theo accountId, giới hạn bằng Pageable
        List<AccountCourse> accountCourses = repository.findAccountCourseByAccountId(
                accountId,
                PageRequest.of(0, size)
        );

        if (accountCourses.isEmpty()) {
            return List.of();
        }

        // Lấy danh sách courseId từ accountCourse
        List<UUID> courseIds = accountCourses.stream()
                .map(AccountCourse::getCourseId)
                .toList();

        // Gọi sang service course để lấy thông tin chi tiết từng khóa học
        var courses = courseService.getCoursesByIds(courseIds);

        // Gộp dữ liệu lại thành danh sách EnrolledCourses (DTO cho từng khóa học)
        return accountCourses.stream()
                .map(ac -> {
                    var course = courses.stream()
                            .filter(c -> c.getId().equals(ac.getCourseId()))
                            .findFirst()
                            .orElse(null);

                    return EnrolledCourses.builder()
                            .id(ac.getCourseId())
                            .name(course != null ? course.getName() : null)
                            .imageUrl(course != null ? course.getImageUrl() : null)
                            .progressPercent(ac.getProgressPercent())
                            .completedLesson(ac.getCompletedLesson())
                            .totalLesson(ac.getTotalLesson())
                            .lastAccessed(ac.getLastAccessed() != null ? ac.getLastAccessed().toString() : null)
                            .slug(course != null ? course.getSlug() : null)
                            .build();
                })
                .toList();
    }

    @Override
    @Cacheable(value = "available_courses", key = "{#accountId, #size}")
    public List<AvailableCourse> getAvailableCourses(UUID accountId, Integer size) {
        Pageable pageable = PageRequest.of(0, size);

        List<Course> availableCourses =
                courseRepository.findAvailableCourses(accountId, pageable);

        return availableCourses.stream()
                .map(course -> AvailableCourse.builder()
                        .id(course.getId())
                        .name(course.getName())
                        .imageUrl(course.getImageUrl())
                        .totalLesson(course.getTotalLessons())
                        .slug(course.getSlug())
                        .price(course.getPrice())
                        .description(course.getDescription())
                        .build())
                .toList();
    }

    @Override
    public LearningDashboard getLearningDashboard(UUID accountId) {
        // Get learning statistics
        LearningStats stats = getLearningStats(accountId);
        
        // Get recent activities (last 5 completed lessons)
        List<RecentActivity> recentActivities = getRecentActivities(accountId, 5);
        
        // Get enrolled courses (last 3)
        List<EnrolledCourses> enrolledCourses = getEnrolledCourses(accountId, 3);
        
        // Get available courses (last 3)
        List<AvailableCourse> availableCourses = getAvailableCourses(accountId, 3);
        
        return LearningDashboard.builder()
                .stats(stats)
                .recentActivities(recentActivities)
                .enrolledCourses(enrolledCourses)
                .availableCourses(availableCourses)
                .build();
    }

    // Method này được gọi từ AccountLessonService khi complete lesson
    public void trackLearningTime(UUID accountId, Integer durationInSeconds) {
        String key = getWeeklyLearningKey(accountId);
        
        try {
            // Increment learning time in seconds
            redisTemplate.opsForValue().increment(key, durationInSeconds);
            
            // Set expiration to end of week (Sunday 23:59:59)
            LocalDateTime endOfWeek = LocalDateTime.now()
                    .with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                    .withHour(23)
                    .withMinute(59)
                    .withSecond(59);
            
            long secondsUntilEndOfWeek = Duration.between(LocalDateTime.now(), endOfWeek).getSeconds();
            redisTemplate.expire(key, secondsUntilEndOfWeek, TimeUnit.SECONDS);
            
            log.info("Auto-tracked {} seconds for account {} (completed lesson)", durationInSeconds, accountId);
        } catch (Exception e) {
            log.error("Error tracking learning time for account {}: {}", accountId, e.getMessage());
        }
    }

    private LearningStats getLearningStats(UUID accountId) {
        // Total courses enrolled
        Long totalCourses = repository.countByAccountId(accountId);
        
        // Completed courses (status = 2)
        Long completedCourses = repository.countByAccountIdAndStatus(accountId, 2);
        
        // In-progress courses (status = 1)
        Long inProgressCourses = repository.countByAccountIdAndStatus(accountId, 1);
        
        // Total lessons completed (status = 2)
        Long totalLessonsCompleted = accountLessonRepository.countByAccountIdAndStatus(accountId, 2);
        
        // Get learning hours this week from Redis
        Double learningHours = getLearningHoursThisWeek(accountId);
        
        return LearningStats.builder()
                .totalCourses(totalCourses != null ? totalCourses.intValue() : 0)
                .completedCourses(completedCourses != null ? completedCourses.intValue() : 0)
                .inProgressCourses(inProgressCourses != null ? inProgressCourses.intValue() : 0)
                .totalLessonsCompleted(totalLessonsCompleted != null ? totalLessonsCompleted.intValue() : 0)
                .learningHoursThisWeek(learningHours)
                .build();
    }

    private List<RecentActivity> getRecentActivities(UUID accountId, int limit) {
        return accountLessonRepository.findRecentCompletedActivities(accountId, limit);
    }

    private Double getLearningHoursThisWeek(UUID accountId) {
        String key = getWeeklyLearningKey(accountId);
        
        try {
            String value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                // Convert seconds to hours
                long seconds = Long.parseLong(value);
                return Math.round(seconds / 3600.0 * 10.0) / 10.0; // Round to 1 decimal place
            }
        } catch (Exception e) {
            log.error("Error getting learning hours for account {}: {}", accountId, e.getMessage());
        }
        
        return 0.0;
    }

    private String getWeeklyLearningKey(UUID accountId) {
        // Get the start of current week (Monday)
        LocalDateTime startOfWeek = LocalDateTime.now()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .withHour(0)
                .withMinute(0)
                .withSecond(0);
        
        String weekKey = startOfWeek.toLocalDate().toString();
        return LEARNING_HOURS_KEY_PREFIX + accountId + ":" + weekKey;
    }

}
