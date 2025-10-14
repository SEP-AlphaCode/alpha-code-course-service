package site.alphacode.alphacodecourseservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateAccountLesson;
import site.alphacode.alphacodecourseservice.dto.response.AccountLessonWithDuration;
import site.alphacode.alphacodecourseservice.dto.response.AccountLessonWithLesson;
import site.alphacode.alphacodecourseservice.dto.response.PagedResult;
import site.alphacode.alphacodecourseservice.service.AccountLessonService;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/account-lessons")
@RequiredArgsConstructor
@Tag(name = "Account Lessons")
public class AccountLessonController {
    private final AccountLessonService accountLessonService;

    @GetMapping("/{id}")
    @Operation(summary = "Get account course by id")
    @PreAuthorize("hasAuthority('ROLE_User')")
    public Optional<AccountLessonWithLesson> getAccountLessonWithLesson(@PathVariable UUID id) {
        return accountLessonService.getAccountLessionWithLessonById(id);
    }

    @GetMapping("")
    @Operation(summary = "Get account lessons with duration and title by courseId and accountId")
    @PreAuthorize("hasAuthority('ROLE_User')")
    public PagedResult<AccountLessonWithDuration> getAccountLessonsWithLessonDurationAndTitleByCourseIdAndAccountId(@RequestParam UUID courseId, @RequestParam UUID accountId, @RequestParam(value = "page", defaultValue = "1") int page,
                                                                                                                    @RequestParam(value = "size", defaultValue = "10") int size) {
        return accountLessonService.getLessonDurationAndTitleByCourseIdAndAccountId(courseId, accountId, page, size);
    }

    @PostMapping()
    @Operation(summary = "Create account lesson")
    @PreAuthorize("hasAuthority('ROLE_User')")
    public AccountLessonWithLesson createAccountLesson(CreateAccountLesson createAccountLesson) {
        return accountLessonService.create(createAccountLesson);
    }

    @PostMapping("/mark-complete/{accountLessonId}")
    @Operation(summary = "Mark account lesson as complete")
    @PreAuthorize("hasAuthority('ROLE_User')")
    public void markAccountLessonAsComplete(@PathVariable UUID accountLessonId) {
        accountLessonService.markComplete(accountLessonId);
    }
}
