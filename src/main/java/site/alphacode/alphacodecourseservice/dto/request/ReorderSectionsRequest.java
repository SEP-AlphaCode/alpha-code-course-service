package site.alphacode.alphacodecourseservice.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import site.alphacode.alphacodecourseservice.dto.response.SectionOrderDto;

import java.util.List;
import java.util.UUID;

@Data
public class ReorderSectionsRequest {
    private UUID courseId;  // Course ID để verify

    @NotNull
    @NotEmpty
    private List<SectionOrderDto> sections;
}

