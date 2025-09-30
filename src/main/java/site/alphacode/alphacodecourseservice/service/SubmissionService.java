package site.alphacode.alphacodecourseservice.service;

import site.alphacode.alphacodecourseservice.dto.request.StaffReviewRequest;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateSubmission;
import site.alphacode.alphacodecourseservice.dto.response.SubmissionDto;

import java.util.UUID;

public interface SubmissionService {
    SubmissionDto createSubmission(CreateSubmission request);
    SubmissionDto getByAccountLessonId(UUID accountLessonId);
    SubmissionDto reviewSubmission(UUID submissionId, StaffReviewRequest request);
}
