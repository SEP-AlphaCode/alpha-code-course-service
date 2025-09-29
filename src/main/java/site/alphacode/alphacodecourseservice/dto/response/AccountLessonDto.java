package site.alphacode.alphacodecourseservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import site.alphacode.alphacodecourseservice.enums.AccountLessonEnum;
import site.alphacode.alphacodecourseservice.enums.CategoryEnum;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AccountLessonDto {
    private UUID id;

    private UUID accountId;

    private UUID lessonId;

    private Integer status;

    private LocalDateTime completedAt;

    private LessonDto  lesson;

    @JsonProperty(value = "statusText")
    public String getStatusText() {
        return AccountLessonEnum.fromCode(this.getStatus());
    }
}

