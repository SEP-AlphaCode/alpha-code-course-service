package site.alphacode.alphacodecourseservice.dto.request.update;

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
public class UpdateCourseBundle {
    @NotNull(message = "Id không được để trống")
    private UUID id;

    @NotNull(message = "BundleId không được để trống")
    private UUID bundleId;

    @NotNull(message = "CourseId không được để trống")
    private UUID courseId;

    @NotNull(message = "Status không được để trống")
    private Integer status;
}
