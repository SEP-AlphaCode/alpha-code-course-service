package site.alphacode.alphacodecourseservice.service;

import site.alphacode.alphacodecourseservice.dto.request.create.CreateSection;
import site.alphacode.alphacodecourseservice.dto.request.update.UpdateSection;
import site.alphacode.alphacodecourseservice.dto.response.PagedResult;
import site.alphacode.alphacodecourseservice.dto.response.SectionDto;

import java.util.UUID;

public interface SectionService {
    SectionDto getById(UUID id);
    PagedResult<SectionDto> getAllByCourseId(UUID courseId, int page, int size);
    SectionDto create(CreateSection createSection);
    SectionDto update(UUID sectionId, UpdateSection updateSection);
    void delete(UUID sectionId);
}
