package site.alphacode.alphacodecourseservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.alphacode.alphacodecourseservice.base.BaseEntityDto;
import site.alphacode.alphacodecourseservice.enums.AccountCourseEnum;
import site.alphacode.alphacodecourseservice.enums.CategoryEnum;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDto extends BaseEntityDto {
    private UUID id;

    @NotBlank(message = "Tên danh mục là bắt buộc")
    @Size(max = 255, message = "Tên danh mục không được vượt quá 255 ký tự")
    private String name;

    @NotBlank(message = "Mô tả danh mục là bắt buộc")
    private String description;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String imageUrl;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @NotBlank(message = "Slug là bắt buộc")
    @Size(max = 255, message = "Slug không được vượt quá 255 ký tự")
    private String slug;

    @JsonProperty(value = "statusText", access = JsonProperty.Access.READ_ONLY)
    public String getStatusText() {
        return CategoryEnum.fromCode(this.getStatus());
    }
}

