package site.alphacode.alphacodecourseservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import site.alphacode.alphacodecourseservice.base.BaseEntityDto;
import site.alphacode.alphacodecourseservice.enums.AccountLessonEnum;
import site.alphacode.alphacodecourseservice.enums.SubmissionEnum;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class SubmissionDto extends BaseEntityDto {
    private UUID id;
    private JsonNode logData;
    private String videoUrl;
    private UUID accountLessonId;
    @JsonProperty(value = "statusText")
    public String getStatusText() {
        return SubmissionEnum.fromCode(this.getStatus());
    }
}

