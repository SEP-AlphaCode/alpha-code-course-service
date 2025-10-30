package site.alphacode.alphacodecourseservice.dto.response;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class SectionOrderDto implements Serializable {
    @NotNull
    private UUID id;

    @NotNull
    @Min(1)
    private Integer orderNumber;
}
