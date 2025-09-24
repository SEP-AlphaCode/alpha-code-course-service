package site.alphacode.alphacodecourseservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.alphacode.alphacodecourseservice.base.BaseEntityDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseBundleDto extends BaseEntityDto {
    private String id;

    @NotNull(message = "Course ID là bắt buộc")
    private String courseId;

    @NotNull(message = "Bundle ID là bắt buộc")
    private String bundleId;
}

