package site.alphacode.alphacodecourseservice.service;

import site.alphacode.alphacodecourseservice.dto.request.ReorderLessonsRequest;
import site.alphacode.alphacodecourseservice.dto.request.ReorderSectionsRequest;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateSection;
import site.alphacode.alphacodecourseservice.dto.request.update.UpdateSection;
import site.alphacode.alphacodecourseservice.dto.response.PagedResult;
import site.alphacode.alphacodecourseservice.dto.response.SectionDto;
import site.alphacode.alphacodecourseservice.dto.response.SectionWithAccountLesson;

import java.util.List;
import java.util.UUID;

public interface  SectionService {
    SectionDto getById(UUID id);
    List<SectionDto> getAllByCourseId(UUID courseId);
    SectionDto create(CreateSection createSection);
    SectionDto update(UUID sectionId, UpdateSection updateSection);
    void delete(UUID sectionId);
    void reorder(UUID courseId ,ReorderSectionsRequest request);
    List<SectionWithAccountLesson> getAllSectionWithAccountLesson(UUID courseId, UUID accountId);
}
