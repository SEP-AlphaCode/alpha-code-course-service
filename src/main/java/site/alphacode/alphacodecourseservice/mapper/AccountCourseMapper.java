package site.alphacode.alphacodecourseservice.mapper;

import site.alphacode.alphacodecourseservice.dto.response.AccountCourseDto;
import site.alphacode.alphacodecourseservice.entity.AccountCourse;

public class AccountCourseMapper {
    public static AccountCourseDto toDto(AccountCourse entity) {
        if (entity == null) {
            return null;
        }
        AccountCourseDto dto = new AccountCourseDto();
        dto.setId(entity.getId());
        dto.setAccountId(entity.getAccountId());
        dto.setCourseId(entity.getCourseId());
        dto.setProgressPercent(entity.getProgressPercent());
        dto.setPurchaseDate(entity.getPurchaseDate());
        dto.setCompleted(entity.getCompleted());
        dto.setStatus(entity.getStatus());
        dto.setCompletedLesson(entity.getCompletedLesson());
        dto.setLastAccessed(entity.getLastAccessed());
        dto.setTotalLesson(entity.getTotalLesson());

        return dto;
    }

    public static AccountCourse toEntity(AccountCourseDto dto) {
        if (dto == null) {
            return null;
        }
        AccountCourse entity = new AccountCourse();
        entity.setId(dto.getId());
        entity.setAccountId(dto.getAccountId());
        entity.setCourseId(dto.getCourseId());
        entity.setProgressPercent(dto.getProgressPercent());
        entity.setPurchaseDate(dto.getPurchaseDate());
        entity.setCompleted(dto.getCompleted());
        entity.setStatus(dto.getStatus());
        entity.setCompletedLesson(dto.getCompletedLesson());
        entity.setLastAccessed(dto.getLastAccessed());
        entity.setTotalLesson(dto.getTotalLesson());

        return entity;
    }

    public static void updateEntityFromDto(AccountCourseDto dto, AccountCourse entity) {
        if (dto == null || entity == null) return;

        entity.setAccountId(dto.getAccountId());
        entity.setCourseId(dto.getCourseId());
        entity.setStatus(dto.getStatus());
        entity.setPurchaseDate(dto.getPurchaseDate());
        entity.setCompleted(dto.getCompleted());
        entity.setTotalLesson(dto.getTotalLesson());
        entity.setCompletedLesson(dto.getCompletedLesson());
        entity.setProgressPercent(dto.getProgressPercent());
    }
}
