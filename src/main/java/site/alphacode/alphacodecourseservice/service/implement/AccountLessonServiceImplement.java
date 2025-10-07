package site.alphacode.alphacodecourseservice.service.implement;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateAccountLesson;
import site.alphacode.alphacodecourseservice.dto.response.AccountLessonWithLesson;
import site.alphacode.alphacodecourseservice.dto.response.AccountLessonWithDuration;
import site.alphacode.alphacodecourseservice.entity.AccountLesson;
import site.alphacode.alphacodecourseservice.entity.Certificate;
import site.alphacode.alphacodecourseservice.exception.ResourceNotFoundException;
import site.alphacode.alphacodecourseservice.mapper.AccountLessonMapper;
import site.alphacode.alphacodecourseservice.repository.AccountCourseRepository;
import site.alphacode.alphacodecourseservice.repository.AccountLessonRepository;
import site.alphacode.alphacodecourseservice.repository.CertificateRepository;
import site.alphacode.alphacodecourseservice.repository.LessonRepository;
import site.alphacode.alphacodecourseservice.repository.CourseRepository;
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

    @Override
    @Cacheable(value = "account_lessons", key = "{#courseId, #accountId, #page, #size}")
      public Page<AccountLessonWithDuration> getLessonDurationAndTitleByCourseIdAndAccountId(UUID courseId, UUID accountId, int page, int size) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("lesson.order_number").ascending());
            accountCourseRepository.updateLastAccessedByAccountIdAndCourseId(courseId, accountId,java.time.LocalDateTime.now());
            return accountLessonRepository.getLessonDurationAndTitleByCourseIdAndAccountId(courseId, accountId, pageable);
      }

      @Override
      @Cacheable(value = "account_lesson_with_lesson", key = "{#accountLessonId}")
      public Optional<AccountLessonWithLesson> getAccountLessionWithLessonById(UUID accountLessonId) {
            var accountLesson = accountLessonRepository.findById(accountLessonId);
            if (accountLesson.isEmpty()) {
                  throw new ResourceNotFoundException("Không tìm thấy bài học: " + accountLessonId);
            }
            var lesson = lessonRepository.findById(accountLesson.get().getLessonId());
            if (lesson.isEmpty()) {
                  throw new ResourceNotFoundException("Không tìm thấy bài học: " + accountLesson.get().getLessonId());
            }

            accountCourseRepository.updateLastAccessedByAccountIdAndCourseId(lesson.get().getCourseId(), accountLesson.get().getAccountId(), LocalDateTime.now());
            var accountWithLesson = AccountLessonMapper.toAccountLessonWithLesson(accountLesson.get(), lesson.get());

            return Optional.ofNullable(accountWithLesson);
      }

      @Override
      @Cacheable(value = "account_lesson", key = "{#accountLessonId}")
      public AccountLessonWithLesson create(CreateAccountLesson createAccountLesson){
            var lesson = lessonRepository.findActiveById(createAccountLesson.getLessonId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học: " + createAccountLesson.getLessonId()));
            AccountLesson accountLesson = new AccountLesson();
            accountLesson.setAccountId(createAccountLesson.getAccountId());
            accountLesson.setLessonId(createAccountLesson.getLessonId());
            accountLesson.setStatus(1); // Mặc định là active
            accountLesson.setCompletedAt(null);

            var saved = accountLessonRepository.save(accountLesson);
            return AccountLessonMapper.toAccountLessonWithLesson(saved,lesson);
      }

      @Override
      @Transactional
      @CachePut(value = "account_lesson_with_lesson", key = "{#accountLessonId}")
      @CacheEvict(value = "account_lessons", allEntries = true)
      public void markComplete(UUID accountLessonId) {
            var accountLesson = accountLessonRepository.findById(accountLessonId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học: " + accountLessonId));
            if (accountLesson.getCompletedAt() != null) {
                  throw new IllegalStateException("Bài học đã được hoàn thành trước đó");
            }

            var lesson = lessonRepository.findById(accountLesson.getLessonId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học: " + accountLesson.getLessonId()));

            accountLesson.setCompletedAt(LocalDateTime.now());
            accountLesson.setStatus(2);
            var updated = accountLessonRepository.save(accountLesson);
            var accountCourse = accountCourseRepository.findByAccountIdAndCourseId(accountLesson.getAccountId(), lesson.getCourseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học cho tài khoản và khóa học tương ứng"));

            accountCourse.setLastAccessed(LocalDateTime.now());
            accountCourse.setCompletedLesson(accountCourse.getCompletedLesson() + 1);
            int progress = (int) Math.round(
                    (accountCourse.getCompletedLesson() * 100.0) / accountCourse.getTotalLesson()
            );
            accountCourse.setProgressPercent(progress);
            if (accountCourse.getCompletedLesson().equals(accountCourse.getTotalLesson())) {
                  accountCourse.setCompleted(true);
                  accountCourse.setStatus(2); // Hoàn thành
                  accountCourseRepository.save(accountCourse);

                    // Tạo chứng chỉ nếu chưa có
                  if (!certificateRepository.existsByAccountIdAndCourseId(accountCourse.getAccountId(), accountCourse.getCourseId())) {
                      Certificate certificate = new Certificate();
                        certificate.setAccountId(accountCourse.getAccountId());
                        certificate.setCourseId(accountCourse.getCourseId());
                        certificate.setIssuedDate(LocalDateTime.now());
                        certificate.setStatus(1);
                        certificateRepository.save(certificate);
                  }
            }

          AccountLessonMapper.toAccountLessonWithLesson(updated, lesson);
      }
}
