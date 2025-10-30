package site.alphacode.alphacodecourseservice.mapper;

import site.alphacode.alphacodecourseservice.dto.response.SectionDto;
import site.alphacode.alphacodecourseservice.dto.response.SectionWithAccountLesson;
import site.alphacode.alphacodecourseservice.entity.Section;

public class SectionMapper {

    public static SectionDto toDto(Section section) {
        return SectionDto.builder()
                .id(section.getId())
                .title(section.getTitle())
                .orderNumber(section.getOrderNumber())
                .courseId(section.getCourseId())
                .createdDate(section.getCreatedDate())
                .status(section.getStatus())
                .lastUpdated(section.getLastUpdated())
                .build();
    }

    public SectionWithAccountLesson toSectionWithAccountLesson(Section section) {
        return SectionWithAccountLesson.builder()
                .id(section.getId())
                .title(section.getTitle())
                .orderNumber(section.getOrderNumber())
                .courseId(section.getCourseId())
                .createdDate(section.getCreatedDate())
                .status(section.getStatus())
                .lastUpdated(section.getLastUpdated())
                .accountLessons(null) // This should be set appropriately in the service layer
                .build();
    }
}
