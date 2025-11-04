package site.alphacode.alphacodecourseservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import site.alphacode.alphacodecourseservice.base.BaseEntityDto;
import site.alphacode.alphacodecourseservice.enums.AccountLessonEnum;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AccountLessonWithLesson extends BaseEntityDto implements Serializable {
    private UUID id;
    private LocalDateTime completedAt;
    private UUID lessonId;
    private UUID accountId;
    private LearnLesson lesson;
    @JsonProperty(value = "typeStatus")
    public String getTypeStatus() {
        return AccountLessonEnum.fromCode(this.getStatus());
    }
}
