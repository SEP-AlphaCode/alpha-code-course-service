package site.alphacode.alphacodecourseservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.alphacode.alphacodecourseservice.dto.response.AccountLessonWithLesson;
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
    public Optional<AccountLessonWithLesson> getAccountLessonWithLesson(@PathVariable UUID id) {
        return accountLessonService.getAccountLessionWithLessonById(id);
    }
}
