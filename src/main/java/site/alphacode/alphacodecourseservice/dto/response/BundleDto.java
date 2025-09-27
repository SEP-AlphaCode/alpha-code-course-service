package site.alphacode.alphacodecourseservice.dto.response;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import site.alphacode.alphacodecourseservice.base.BaseEntityDto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BundleDto extends BaseEntityDto implements Serializable {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private String coverImage;
}

