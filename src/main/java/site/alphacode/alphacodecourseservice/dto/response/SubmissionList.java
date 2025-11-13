package site.alphacode.alphacodecourseservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import site.alphacode.alphacodecourseservice.base.BaseEntityDto;
import site.alphacode.alphacodecourseservice.enums.SubmissionEnum;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class SubmissionList extends BaseEntityDto implements Serializable {
    private UUID id;
    private UUID accountLessonId;
    private UUID accountId;
    private String accountName;
    private UUID lessonId;
    private String lessonTitle;
    private String videoUrl;

    @JsonProperty(value = "statusText")
    public String getStatusText() {
        return SubmissionEnum.fromCode(this.getStatus());
    }
}
