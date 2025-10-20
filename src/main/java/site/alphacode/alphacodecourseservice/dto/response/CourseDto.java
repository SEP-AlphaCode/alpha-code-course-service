package site.alphacode.alphacodecourseservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import site.alphacode.alphacodecourseservice.base.BaseEntityDto;
import site.alphacode.alphacodecourseservice.enums.CourseLevelEnum;
import site.alphacode.alphacodecourseservice.enums.CourseStatusEnum;

import java.io.Serializable;
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

    private String categoryName;

    private Integer sectionCount;

    @JsonProperty(value = "statusText")
    public String getStatusText() {
        return CourseStatusEnum.fromCode(this.getStatus());
    }

    @JsonProperty(value = "levelText")
    public String getLevelText() {
        return CourseLevelEnum.fromCode(this.getLevel());
    }
}

