package site.alphacode.alphacodecourseservice.mapper;

import site.alphacode.alphacodecourseservice.dto.response.SubmissionDetail;
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

    public static SubmissionDetail toDetail(Submission submission, String accountName) {
        if (submission == null) {
            return null;
        }
        return SubmissionDetail.builder()
                .id(submission.getId())
                .logData(submission.getLogData())
                .videoUrl(submission.getVideoUrl())
                .accountLessonId(submission.getAccountLessonId())
                .createdDate(submission.getCreatedDate())
                .lastUpdated(submission.getLastUpdated())
                .accountId(submission.getAccountLesson().getAccountId())
                .accountName(accountName)
                .missingActions(submission.getMissingActions())
                .lessonId(submission.getAccountLesson().getLessonId())
                .lessonTitle(submission.getAccountLesson().getLesson().getTitle())
                .staffComment(submission.getStaffComment())
                .status(submission.getStatus())
                .build();
    }
}
