package site.alphacode.alphacodecourseservice.service;

import site.alphacode.alphacodecourseservice.dto.request.StaffReviewRequest;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateSubmission;
import site.alphacode.alphacodecourseservice.dto.response.PagedResult;
import site.alphacode.alphacodecourseservice.dto.response.SubmissionDetail;
import site.alphacode.alphacodecourseservice.dto.response.SubmissionDto;
import site.alphacode.alphacodecourseservice.dto.response.SubmissionList;

import java.util.UUID;

public interface SubmissionService {
    SubmissionDto createSubmission(CreateSubmission request);

    SubmissionDto getByAccountLessonId(UUID accountLessonId);

    SubmissionDto reviewSubmission(UUID submissionId, StaffReviewRequest request);

    PagedResult<SubmissionList> getUnreviewedSubmissions(int page, int size);

    PagedResult<SubmissionList> getFailedSubmissions(int page, int size);
    SubmissionDetail getSubmissionDetail(UUID submissionId);
}