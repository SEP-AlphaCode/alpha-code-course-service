package site.alphacode.alphacodecourseservice.dto.request.create;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateAccountLesson {
    @NotNull(message = "Tài khoản là bắt buộc")
    private UUID accountId;

    @NotNull(message = "Bài học là bắt buộc")
    private UUID lessonId;
}
