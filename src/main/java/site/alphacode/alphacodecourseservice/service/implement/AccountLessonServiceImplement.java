package site.alphacode.alphacodecourseservice.service.implement;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateAccountLesson;
import site.alphacode.alphacodecourseservice.dto.response.AccountLessonWithLesson;
import site.alphacode.alphacodecourseservice.dto.response.AccountLessonWithDuration;
import site.alphacode.alphacodecourseservice.dto.response.PagedResult;
import site.alphacode.alphacodecourseservice.entity.AccountLesson;
import site.alphacode.alphacodecourseservice.entity.Certificate;
import site.alphacode.alphacodecourseservice.exception.ResourceNotFoundException;
import site.alphacode.alphacodecourseservice.mapper.AccountLessonMapper;
import site.alphacode.alphacodecourseservice.producer.CourseProducer;
import site.alphacode.alphacodecourseservice.repository.AccountCourseRepository;
import site.alphacode.alphacodecourseservice.repository.AccountLessonRepository;
import site.alphacode.alphacodecourseservice.repository.CertificateRepository;
import site.alphacode.alphacodecourseservice.repository.LessonRepository;
import site.alphacode.alphacodecourseservice.service.AccountLessonService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AccountLessonServiceImplement implements AccountLessonService {
      private final AccountLessonRepository accountLessonRepository;
      private final AccountCourseRepository accountCourseRepository;
      private final LessonRepository lessonRepository;
      private final CertificateRepository certificateRepository;
      private final AccountCourseServiceImplement accountCourseService;
    private final CourseProducer courseProducer;

    @Override
    @Cacheable(value = "account_lessons", key = "{#courseId, #accountId, #page, #size}")
    @Transactional
    public PagedResult<AccountLessonWithDuration> getLessonDurationAndTitleByCourseIdAndAccountId(UUID courseId, UUID accountId, int page, int size) {
            Pageable pageable = PageRequest.of(page - 1, size);
            accountCourseRepository.updateLastAccessedByAccountIdAndCourseId(courseId, accountId,java.time.LocalDateTime.now());
            Page<AccountLessonWithDuration> pageResult = accountLessonRepository.getLessonDurationAndTitleByCourseIdAndAccountId(courseId, accountId, pageable);
            return new PagedResult<>(pageResult);
      }

    @Override
    @Cacheable(value = "account_lesson_with_lesson", key = "{#accountLessonId}")
    @Transactional
    public Optional<AccountLessonWithLesson> getAccountLessonWithLessonById(UUID accountLessonId) {
        AccountLesson accountLesson = accountLessonRepository.findById(accountLessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học: " + accountLessonId));

        var lesson = lessonRepository.findById(accountLesson.getLessonId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học: " + accountLesson.getLessonId()));

        accountCourseRepository.updateLastAccessedByAccountIdAndCourseId(
                lesson.getSection().getCourseId(),
                accountLesson.getAccountId(),
                LocalDateTime.now()
        );

        if(accountLesson.getStatus() == 0){
            accountLesson.setStatus(1); // Cập nhật trạng thái từ Chưa bắt đầu (0) sang Đang học (1)
        }
        accountLesson.setLastUpdated(LocalDateTime.now());
        accountLessonRepository.save(accountLesson);

        return Optional.ofNullable(AccountLessonMapper.toAccountLessonWithLesson(accountLesson, lesson));
    }

    @Override
    @Cacheable(value = "account_lesson_by_account_course", key = "{#createAccountLesson.accountId, #createAccountLesson.lessonId}")
    @CacheEvict(value = {"account_lessons", "account_lesson_with_lesson"}, allEntries = true)
    public AccountLessonWithLesson create(CreateAccountLesson createAccountLesson){
        // Lấy Lesson + Section trong 1 query
        var lesson = lessonRepository.findActiveWithSectionById(createAccountLesson.getLessonId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học: " + createAccountLesson.getLessonId()));

        var accountLessonOpt = accountLessonRepository
                .findByAccountIdAndLessonId(createAccountLesson.getAccountId(), createAccountLesson.getLessonId());
        if (accountLessonOpt.isPresent()) {
            throw new IllegalStateException("Bài học đã được tạo cho tài khoản này.");
        }

        // Lấy AccountCourse
        var accountCourse = accountCourseRepository.findByAccountIdAndCourseId(
                createAccountLesson.getAccountId(),
                lesson.getSection().getCourseId()
        ).orElseThrow(() -> new ResourceNotFoundException("Tài khoản chưa đăng ký khóa học chứa bài học này."));

        // Tạo AccountLesson
        AccountLesson accountLesson = AccountLesson.builder()
                .accountId(accountCourse.getAccountId())
                .lessonId(createAccountLesson.getLessonId())
                .status(1)
                .completedAt(null)
                .createdDate(LocalDateTime.now())
                .build();

        AccountLesson saved = accountLessonRepository.save(accountLesson);

        return AccountLessonMapper.toAccountLessonWithLesson(saved, lesson);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @CacheEvict(value = {"account_lessons", "account_lesson_with_lesson"}, allEntries = true)
    public void markComplete(UUID accountLessonId) {
        AccountLesson accountLesson = accountLessonRepository.findById(accountLessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học: " + accountLessonId));

        if (accountLesson.getCompletedAt() != null) {
            throw new IllegalStateException("Bài học đã được hoàn thành trước đó");
        }

        var lesson = lessonRepository.findById(accountLesson.getLessonId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học: " + accountLesson.getLessonId()));

        // Cập nhật trạng thái
        accountLesson.setCompletedAt(LocalDateTime.now());
        accountLesson.setStatus(2);
        accountLesson.setLastUpdated(LocalDateTime.now());
        AccountLesson updated = accountLessonRepository.save(accountLesson);

        // Track learning time (duration của lesson) vào Redis
        accountCourseService.trackLearningTime(accountLesson.getAccountId(), lesson.getDuration());

        // Cập nhật tiến độ khóa học
        var accountCourse = accountCourseRepository.findByAccountIdAndCourseId(accountLesson.getAccountId(), lesson.getSection().getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học cho tài khoản và khóa học tương ứng"));

        accountCourse.setLastAccessed(LocalDateTime.now());
        accountCourse.setCompletedLesson(accountCourse.getCompletedLesson() + 1);
        int progress = (int) Math.round((accountCourse.getCompletedLesson() * 100.0) / accountCourse.getTotalLesson());
        accountCourse.setProgressPercent(progress);

        if (accountCourse.getCompletedLesson().equals(accountCourse.getTotalLesson())) {
            accountCourse.setCompleted(true);
            accountCourse.setStatus(2);
            accountCourseRepository.save(accountCourse);

            if (!certificateRepository.existsByAccountIdAndCourseId(accountCourse.getAccountId(), accountCourse.getCourseId())) {
                Certificate certificate = new Certificate();
                certificate.setAccountId(accountCourse.getAccountId());
                certificate.setCourseId(accountCourse.getCourseId());
                certificate.setIssuedDate(LocalDateTime.now());
                certificate.setStatus(1);
                certificateRepository.save(certificate);
            }

            courseProducer.sendCourseCompletedMessage(accountCourse.getAccountId().toString(), accountCourse.getCourseId().toString(), accountCourse.getCourse().getName());
        }
    }
}
