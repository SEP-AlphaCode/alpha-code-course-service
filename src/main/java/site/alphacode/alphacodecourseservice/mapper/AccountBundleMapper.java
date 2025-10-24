package site.alphacode.alphacodecourseservice.mapper;

import site.alphacode.alphacodecourseservice.dto.response.AccountBundleDto;
import site.alphacode.alphacodecourseservice.entity.AccountBundle;

public class AccountBundleMapper {
    public static AccountBundleDto toDto(AccountBundle entity) {
        if (entity == null) {
            return null;
        }
        AccountBundleDto dto = new AccountBundleDto();
        dto.setId(entity.getId());
        dto.setAccountId(entity.getAccountId());
        dto.setBundleId(entity.getBundleId());
        dto.setStatus(entity.getStatus());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setLastUpdated(entity.getLastUpdated());
        return dto;
    }
}
