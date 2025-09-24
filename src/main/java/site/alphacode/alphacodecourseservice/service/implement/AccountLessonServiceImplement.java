package site.alphacode.alphacodecourseservice.service.implement;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import site.alphacode.alphacodecourseservice.dto.response.AccountLessonWithLesson;
import site.alphacode.alphacodecourseservice.dto.response.AccountLessonWithDuration;
import site.alphacode.alphacodecourseservice.exception.ResourceNotFoundException;
import site.alphacode.alphacodecourseservice.mapper.AccountLessonMapper;
import site.alphacode.alphacodecourseservice.repository.AccountCourseRepository;
import site.alphacode.alphacodecourseservice.repository.AccountLessonRepository;
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

      @Override
      @Cacheable(value = "account_lessons", key = "{#courseId, #accountId, #page, #size}")
      public Page<AccountLessonWithDuration> getLessonDurationAndTitleByCourseIdAndAccountId(UUID courseId, UUID accountId, int page, int size) {
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by("lesson.order_number").ascending());
            accountCourseRepository.updateLastAccessedByAccountIdAndCourseId(courseId, accountId,java.time.LocalDateTime.now());
            return accountLessonRepository.getLessonDurationAndTitleByCourseIdAndAccountId(courseId, accountId, pageable);
      }

      @Override
      @Cacheable(value = "account_lesson_with_lesson", key = "#accountLessonId")
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
}
