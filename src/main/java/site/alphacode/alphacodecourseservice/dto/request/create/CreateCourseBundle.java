package site.alphacode.alphacodecourseservice.dto.request.create;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCourseBundle {
    @NotEmpty(message = "BundleId không được để trống")
    private UUID bundleId;

    @NotNull(message = "Danh sách courseId không được để trống")
    private List<UUID> courseIds;
}
