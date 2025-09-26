package site.alphacode.alphacodecourseservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.alphacode.alphacodecourseservice.base.BaseEntityDto;
import site.alphacode.alphacodecourseservice.enums.AccountCourseEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseDto extends BaseEntityDto implements Serializable {
    private UUID id;

    private String name;

    private String description;

    private BigDecimal price;

    private Boolean requireLicense;

    private Integer level;

    private Integer totalLessons;

    private Integer totalDuration;

    private String imageUrl;

    private String slug;

    private UUID categoryId;

    @JsonProperty(value = "statusText", access = JsonProperty.Access.READ_ONLY)
    public String getStatusText() {
        return AccountCourseEnum.fromCode(this.getStatus());
    }
}

