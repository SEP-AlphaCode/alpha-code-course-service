package site.alphacode.alphacodecourseservice.service;

import site.alphacode.alphacodecourseservice.dto.request.create.CreateAccountLesson;
import site.alphacode.alphacodecourseservice.dto.response.AccountLessonWithLesson;
import site.alphacode.alphacodecourseservice.dto.response.AccountLessonWithDuration;
import site.alphacode.alphacodecourseservice.dto.response.PagedResult;

import java.util.Optional;
import java.util.UUID;

public interface AccountLessonService {
    PagedResult<AccountLessonWithDuration> getLessonDurationAndTitleByCourseIdAndAccountId(UUID courseId, UUID accountId, int page, int size);
    Optional<AccountLessonWithLesson> getAccountLessonWithLessonById(UUID accountLessonId);
    AccountLessonWithLesson create(CreateAccountLesson createAccountLesson);
    void markComplete(UUID accountLessonId);
}

