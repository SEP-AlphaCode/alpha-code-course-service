package site.alphacode.alphacodecourseservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import site.alphacode.alphacodecourseservice.base.BaseEntityDto;
import site.alphacode.alphacodecourseservice.enums.AccountLessonEnum;
import site.alphacode.alphacodecourseservice.enums.CategoryEnum;
import site.alphacode.alphacodecourseservice.enums.SubmissionEnum;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AccountLessonDto extends BaseEntityDto implements Serializable {
    private UUID id;

    private UUID accountId;

    private UUID lessonId;

    private LocalDateTime completedAt;

    private LessonDto  lesson;

    private Integer submissionStatus;

    @JsonProperty(value = "statusText")
    public String getStatusText() {
        return AccountLessonEnum.fromCode(this.getStatus());
    }

    @JsonProperty(value = "submissionStatusText")
    public String getSubmissionStatusText() {
        return SubmissionEnum.fromCode(this.getSubmissionStatus());
    }
}

