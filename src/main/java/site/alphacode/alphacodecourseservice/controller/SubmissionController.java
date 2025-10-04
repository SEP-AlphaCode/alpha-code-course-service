package site.alphacode.alphacodecourseservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import site.alphacode.alphacodecourseservice.dto.request.StaffReviewRequest;
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
    public SubmissionDto getByAccountLessonId(@PathVariable UUID accountLessonId) {
         return submissionService.getByAccountLessonId(accountLessonId);
    }

    @PostMapping()
    @Operation(summary = "Create a new submission")
    @PreAuthorize("hasAuthority('ROLE_User')")
    public SubmissionDto createSubmission(CreateSubmission createSubmission) {
        return submissionService.createSubmission(createSubmission);
    }

    @PutMapping("/{submissionId}/review")
    @Operation(summary = "Staff review a submission")
    @PreAuthorize("hasAuthority('ROLE_Staff')")
    public SubmissionDto reviewSubmission(
            @PathVariable UUID submissionId,
            @RequestBody StaffReviewRequest request
    ) {
        return submissionService.reviewSubmission(submissionId, request);
    }
}
