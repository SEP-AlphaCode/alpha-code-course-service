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
public class CreateCourseBundle {
    @NotNull(message = "BundleId không được để trống")
    private UUID bundleId;

    @NotNull(message = "CourseId không được để trống")
    private UUID courseId;
}
