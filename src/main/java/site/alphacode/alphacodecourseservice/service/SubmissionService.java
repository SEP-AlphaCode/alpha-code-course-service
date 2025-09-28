package site.alphacode.alphacodecourseservice.service;

import site.alphacode.alphacodecourseservice.dto.request.create.CreateSubmission;
import site.alphacode.alphacodecourseservice.dto.response.SubmissionDto;

public interface SubmissionService {
    SubmissionDto createSubmission(CreateSubmission request);
}
