package site.alphacode.alphacodecourseservice.mapper;

import site.alphacode.alphacodecourseservice.dto.response.SubmissionDto;
import site.alphacode.alphacodecourseservice.entity.Submission;

public class SubmissionMapper {
    public static SubmissionDto toDto(Submission submission) {
        if (submission == null) {
            return null;
        }
        return SubmissionDto.builder()
                .id(submission.getId())
                .logData(submission.getLogData())
                .videoUrl(submission.getVideoUrl())
                .accountLessonId(submission.getAccountLessonId())
                .createdDate(submission.getCreatedDate())
                .lastUpdated(submission.getLastUpdated())
                .status(submission.getStatus())
                .build();
    }
}
