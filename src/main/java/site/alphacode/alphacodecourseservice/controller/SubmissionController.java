package site.alphacode.alphacodecourseservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import site.alphacode.alphacodecourseservice.dto.request.StaffReviewRequest;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateSubmission;
import site.alphacode.alphacodecourseservice.dto.response.*;
import site.alphacode.alphacodecourseservice.service.S3Service;
import site.alphacode.alphacodecourseservice.service.SubmissionService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/submissions")
@RequiredArgsConstructor
@Tag(name = "Submission", description = "Submission management APIs")
public class SubmissionController {
    private final SubmissionService submissionService;
    private final S3Service s3Service;

    @GetMapping("/by-account-lesson-id/{accountLessonId}")
    @Operation(summary = "Get newest submission by account lesson ID")
    public SubmissionDto getByAccountLessonId(@PathVariable UUID accountLessonId) {
         return submissionService.getByAccountLessonId(accountLessonId);
    }

    @GetMapping("/presign")
    @Operation(summary = "Generate S3 presigned PUT URL for direct upload (Children and Parent only)")
    @PreAuthorize("hasAnyAuthority('ROLE_Parent', 'ROLE_Children')")
    public PresignResponse presign(
            @RequestParam @NotBlank String filename,
            @RequestParam(defaultValue = "video/mp4") String contentType,
            @RequestParam(defaultValue = "lessons") String folder,
            @RequestParam(defaultValue = "900") long expiresInSeconds
    ) {
        return s3Service.generatePresignUrl(filename, contentType, folder, expiresInSeconds);
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
