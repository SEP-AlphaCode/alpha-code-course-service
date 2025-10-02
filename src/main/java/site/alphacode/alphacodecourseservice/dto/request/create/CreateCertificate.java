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
public class CreateCertificate {
    @NotNull(message = "Id người dùng là bắt buộc")
    private UUID accountId;
    @NotNull(message = "Id khóa học là bắt buộc")
    private UUID courseId;
}
