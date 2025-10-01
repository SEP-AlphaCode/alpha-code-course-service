package site.alphacode.alphacodecourseservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import site.alphacode.alphacodecourseservice.base.BaseEntityDto;
import site.alphacode.alphacodecourseservice.enums.AccountCourseEnum;
import site.alphacode.alphacodecourseservice.enums.CourseEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CourseDto extends BaseEntityDto implements Serializable {
    private UUID id;

    private String name;

    private String description;

    private Integer price;

    private Boolean requireLicense;

    private Integer level;

    private Integer totalLessons;

    private Integer totalDuration;

    private String imageUrl;

    private String slug;

    private UUID categoryId;

    @JsonProperty(value = "statusText")
    public String getStatusText() {
        return CourseEnum.fromCode(this.getStatus());
    }

    @JsonProperty(value = "levelText")
    public String getLevelText() {
        return CourseEnum.fromCode(this.getLevel());
    }
}

