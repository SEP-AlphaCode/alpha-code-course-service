package site.alphacode.alphacodecourseservice.dto.request.patch;

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
public class PatchCourseBundle {
    private UUID bundleId;

    private UUID courseId;

    private Integer status;
}
