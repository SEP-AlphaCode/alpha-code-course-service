package site.alphacode.alphacodecourseservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import site.alphacode.alphacodecourseservice.base.BaseEntityDto;
import site.alphacode.alphacodecourseservice.enums.AccountLessonEnum;
import site.alphacode.alphacodecourseservice.enums.BundleEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BundleDto extends BaseEntityDto implements Serializable {
    private UUID id;
    private String name;
    private String description;
    private Integer price;
    private Integer discountPrice;
    private String coverImage;
    private List<UUID> courseIds;
    @JsonProperty(value = "statusText")
    public String getStatusText() {
        return BundleEnum.fromCode(this.getStatus());
    }
}

