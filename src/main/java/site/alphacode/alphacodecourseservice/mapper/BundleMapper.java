package site.alphacode.alphacodecourseservice.mapper;

import site.alphacode.alphacodecourseservice.dto.response.BundleDto;
import site.alphacode.alphacodecourseservice.entity.Bundle;

public class BundleMapper {
    public static BundleDto toDto(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        BundleDto bundleDto = new BundleDto();
        bundleDto.setId(bundle.getId());
        bundleDto.setName(bundle.getName());
        bundleDto.setDescription(bundle.getDescription());
        bundleDto.setPrice(bundle.getPrice());
        bundleDto.setDiscountPrice(bundle.getDiscountPrice());
        bundleDto.setCoverImage(bundle.getCoverImage());
        bundleDto.setCreatedDate(bundle.getCreatedDate());
        bundleDto.setLastUpdated(bundle.getLastUpdated());
        bundleDto.setStatus(bundle.getStatus());
        if (bundle.getCourseBundles() != null) {
            bundleDto.setCourseIds(
                    bundle.getCourseBundles()
                            .stream()
                            .map(cb -> cb.getCourse().getId()) // lấy id course
                            .toList()
            );
        }
        return bundleDto;
    }
}
