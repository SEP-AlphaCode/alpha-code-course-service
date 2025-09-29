package site.alphacode.alphacodecourseservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import site.alphacode.alphacodecourseservice.base.BaseEntityDto;
import site.alphacode.alphacodecourseservice.enums.AccountLessonEnum;
import site.alphacode.alphacodecourseservice.enums.CourseBundleEnum;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CourseBundleDto extends BaseEntityDto {
    private UUID id;

    private UUID courseId;

    private UUID bundleId;

    @JsonProperty(value = "statusText")
    public String getStatusText() {
        return CourseBundleEnum.fromCode(this.getStatus());
    }
}

