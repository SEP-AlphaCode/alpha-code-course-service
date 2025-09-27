package site.alphacode.alphacodecourseservice.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import site.alphacode.alphacodecourseservice.base.BaseEntityDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CourseBundleDto extends BaseEntityDto {
    private String id;

    @NotNull(message = "Course ID là bắt buộc")
    private String courseId;

    @NotNull(message = "Bundle ID là bắt buộc")
    private String bundleId;
}

