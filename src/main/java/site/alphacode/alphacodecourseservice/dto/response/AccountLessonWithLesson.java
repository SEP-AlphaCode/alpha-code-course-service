package site.alphacode.alphacodecourseservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountLessonWithLesson {
    private UUID id;
    private Integer status;
    private LocalDateTime completedAt;
    private UUID lessonId;
    private UUID accountId;
    private LearnLesson lesson;
}
