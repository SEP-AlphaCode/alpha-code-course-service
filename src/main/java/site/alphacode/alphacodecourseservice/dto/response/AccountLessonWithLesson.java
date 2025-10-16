package site.alphacode.alphacodecourseservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import site.alphacode.alphacodecourseservice.enums.AccountLessonEnum;
import site.alphacode.alphacodecourseservice.enums.LessonEnum;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AccountLessonWithLesson {
    private UUID id;
    private Integer status;
    private LocalDateTime completedAt;
    private UUID lessonId;
    private UUID accountId;
    private LearnLesson lesson;
    @JsonProperty(value = "typeStatus")
    public String getTypeStatus() {
        return AccountLessonEnum.fromCode(this.getStatus());
    }
}
