package site.alphacode.alphacodecourseservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import site.alphacode.alphacodecourseservice.base.BaseEntityDto;
import site.alphacode.alphacodecourseservice.enums.SectionStatusEnum;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class SectionDto extends BaseEntityDto implements Serializable {
    private UUID id;
    private String title;
    private Integer orderNumber;
    private UUID courseId;
    // Add lessons list to include lessons inside section in responses
    private List<LessonDto> lessons;

    @JsonProperty(value = "statusText")
    public String getStatusText() {
        return SectionStatusEnum.fromCode(this.getStatus());
    }
}
