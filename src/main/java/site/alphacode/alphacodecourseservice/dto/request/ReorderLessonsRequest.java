package site.alphacode.alphacodecourseservice.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import site.alphacode.alphacodecourseservice.dto.response.LessonOrderDto;

import java.util.List;

@Data
public class ReorderLessonsRequest {
    @NotNull
    @NotEmpty
    private List<LessonOrderDto> lessons;
}

