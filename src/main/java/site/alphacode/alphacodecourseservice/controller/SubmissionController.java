package site.alphacode.alphacodecourseservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateSubmission;
import site.alphacode.alphacodecourseservice.dto.response.SubmissionDto;
import site.alphacode.alphacodecourseservice.service.SubmissionService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/submissions")
@RequiredArgsConstructor
@Tag(name = "Submission", description = "Submission management APIs")
public class SubmissionController {
    private final SubmissionService submissionService;

    @GetMapping("/by-account-lesson-id/{accountLessonId}")
    @Operation(summary = "Get newest submission by account lesson ID")
    public SubmissionDto getByAccountLessonId(UUID accountLessonId) {
         return submissionService.getByAccountLessonId(accountLessonId);
    }

    @PostMapping()
    @Operation(summary = "Create a new submission")
    @PreAuthorize("hasAuthority('ROLE_User')")
    public SubmissionDto createSubmission(CreateSubmission createSubmission) {
        return submissionService.createSubmission(createSubmission);
    }
}
