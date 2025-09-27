package site.alphacode.alphacodecourseservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import site.alphacode.alphacodecourseservice.base.BaseEntityDto;
import site.alphacode.alphacodecourseservice.enums.CategoryEnum;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CategoryDto extends BaseEntityDto implements Serializable {
    private UUID id;

    private String name;

    private String description;

    private String imageUrl;

    private String slug;

    @JsonProperty(value = "statusText")
    public String getStatusText() {
        return CategoryEnum.fromCode(this.getStatus());
    }
}

