package site.alphacode.alphacodecourseservice.mapper;

import site.alphacode.alphacodecourseservice.dto.response.BundleDto;
import site.alphacode.alphacodecourseservice.entity.Bundle;

public class BundleMapper {
    public static BundleDto toDto(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        return BundleDto.builder()
                .id(bundle.getId())
                .name(bundle.getName())
                .description(bundle.getDescription())
                .price(bundle.getPrice())
                .discountPrice(bundle.getDiscountPrice())
                .coverImage(bundle.getCoverImage())
                .createdDate(bundle.getCreatedDate())
                .lastUpdated(bundle.getLastUpdated())
                .status(bundle.getStatus())
                .build();
    }
}
