package site.alphacode.alphacodecourseservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import site.alphacode.alphacodecourseservice.dto.request.StaffReviewRequest;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateSubmission;
import site.alphacode.alphacodecourseservice.dto.response.PagedResult;
import site.alphacode.alphacodecourseservice.dto.response.SubmissionDetail;
import site.alphacode.alphacodecourseservice.dto.response.SubmissionDto;
import site.alphacode.alphacodecourseservice.dto.response.SubmissionList;
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a new submission")
    @PreAuthorize("hasAnyAuthority('ROLE_Parent', 'ROLE_Children')")
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

    @GetMapping("/unreviewed")
    @Operation(summary = "Get list unreviewed submissions with pagination")
    @PreAuthorize("hasAuthority('ROLE_Staff')")
    public PagedResult<SubmissionList> getUnreviewedSubmissions(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return submissionService.getUnreviewedSubmissions(page, size);
    }

    @GetMapping("/failed")
    @Operation(summary = "Get list failed submissions with pagination")
    @PreAuthorize("hasAuthority('ROLE_Staff')")
    public PagedResult<SubmissionList> getFailedSubmissions(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return submissionService.getFailedSubmissions(page, size);
    }

    @GetMapping("/detail/{submissionId}")
    @Operation(summary = "Get submission detail by ID")
    @PreAuthorize("hasAuthority('ROLE_Staff')")
    public SubmissionDetail getSubmissionDetail(@PathVariable UUID submissionId) {
        return submissionService.getSubmissionDetail(submissionId);
    }
}
