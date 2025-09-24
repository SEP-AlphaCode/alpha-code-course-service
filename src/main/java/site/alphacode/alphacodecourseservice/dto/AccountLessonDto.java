package site.alphacode.alphacodecourseservice.dto;

import jakarta.validation.constraints.NotNull;
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
public class AccountLessonDto {
    private UUID id;

    @NotNull(message = "Tài khoản là bắt buộc")
    private UUID accountId;

    @NotNull(message = "Bài học là bắt buộc")
    private UUID lessonId;

    @NotNull(message = "Trạng thái là bắt buộc")
    private Integer status;

    private LocalDateTime completedAt;

    private LessonDto lesson;
}

