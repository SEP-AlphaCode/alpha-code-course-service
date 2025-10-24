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
public class CreateAccountBundle {
    @NotNull(message = "ID tài khoản là bắt buộc")
    private UUID accountId;

    @NotNull(message = "ID gói học là bắt buộc")
    private UUID bundleId;
}
