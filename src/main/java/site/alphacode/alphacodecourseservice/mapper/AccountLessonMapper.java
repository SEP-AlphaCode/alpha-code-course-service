package site.alphacode.alphacodecourseservice.mapper;

import site.alphacode.alphacodecourseservice.dto.response.AccountLessonDto;
import site.alphacode.alphacodecourseservice.dto.response.AccountLessonWithLesson;
import site.alphacode.alphacodecourseservice.entity.AccountLesson;
import site.alphacode.alphacodecourseservice.entity.Lesson;

public class AccountLessonMapper {
    public static AccountLessonDto toAccountLessonDto(AccountLesson entity) {
        if (entity == null) {
            return null;
        }
        AccountLessonDto dto = new AccountLessonDto();
        dto.setId(entity.getId());
        dto.setAccountId(entity.getAccountId());
        dto.setLessonId(entity.getLessonId());
        dto.setStatus(entity.getStatus());
        dto.setCompletedAt(entity.getCompletedAt());
        return dto;
    }

    public static AccountLessonWithLesson toAccountLessonWithLesson(AccountLesson accountLesson, Lesson lesson) {
        if (accountLesson == null || lesson == null) {
            return null;
        }


        AccountLessonWithLesson dto = new AccountLessonWithLesson();
        dto.setId(accountLesson.getId());
        dto.setAccountId(accountLesson.getAccountId());
        dto.setLessonId(accountLesson.getLessonId());
        dto.setStatus(accountLesson.getStatus());
        dto.setCompletedAt(accountLesson.getCompletedAt());
        dto.setLesson(LessonMapper.toLearnLesson(lesson));

        return dto;
    }
}
