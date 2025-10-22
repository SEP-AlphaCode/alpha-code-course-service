package site.alphacode.alphacodecourseservice.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import site.alphacode.alphacodecourseservice.dto.response.SectionOrderDto;

import java.util.List;
import java.util.UUID;

@Data
public class ReorderSectionsRequest {
    @NotNull
    @NotEmpty
    private List<SectionOrderDto> sections;
}

