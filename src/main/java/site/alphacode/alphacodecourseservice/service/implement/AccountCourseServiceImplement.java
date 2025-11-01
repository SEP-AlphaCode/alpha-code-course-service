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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.alphacode.alphacodecourseservice.dto.response.AccountCourseDto;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateAccountCourse;
import site.alphacode.alphacodecourseservice.dto.response.PagedResult;
import site.alphacode.alphacodecourseservice.entity.AccountCourse;
import site.alphacode.alphacodecourseservice.exception.ConflictException;
import site.alphacode.alphacodecourseservice.mapper.AccountCourseMapper;
import site.alphacode.alphacodecourseservice.repository.AccountCourseRepository;
import site.alphacode.alphacodecourseservice.repository.CourseBundleRepository;
import site.alphacode.alphacodecourseservice.repository.LessonRepository;
import site.alphacode.alphacodecourseservice.service.AccountCourseService;
import site.alphacode.alphacodecourseservice.service.CourseService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountCourseServiceImplement implements AccountCourseService {
    private final AccountCourseRepository repository;
    private final LessonRepository lessonRepository;
    private final CourseBundleRepository courseBundleRepository;
    private final CourseService courseService;

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
    @CacheEvict(value = {"account_courses", "account_course"}, allEntries = true)
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
    @CacheEvict(value = {"account_courses", "account_course"}, allEntries = true)
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
    @CacheEvict(value = {"account_courses", "account_course"}, allEntries = true)
    public void delete(UUID id) {
        var accountCourse = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy AccountCourse với id: " + id));
        repository.softDeleteById(accountCourse.getId());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"account_courses", "account_course"}, allEntries = true)
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

}
