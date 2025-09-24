package site.alphacode.alphacodecourseservice.service;

import org.springframework.data.domain.Page;
import site.alphacode.alphacodecourseservice.dto.response.AccountLessonWithLesson;
import site.alphacode.alphacodecourseservice.dto.response.AccountLessonWithDuration;

import java.util.Optional;
import java.util.UUID;

public interface AccountLessonService {
    Page<AccountLessonWithDuration> getLessonDurationAndTitleByCourseIdAndAccountId(UUID courseId, UUID accountId, int page, int size);
    Optional<AccountLessonWithLesson> getAccountLessionWithLessonById(UUID accountLessonId);
}
