package site.alphacode.alphacodecourseservice.service;

import site.alphacode.alphacodecourseservice.dto.request.create.CreateAccountBundle;
import site.alphacode.alphacodecourseservice.dto.response.AccountBundleDto;
import site.alphacode.alphacodecourseservice.dto.response.PagedResult;

import java.util.UUID;

public interface AccountBundleService {
    AccountBundleDto getByAccountIdAndBundleId(UUID accountId, UUID bundleId);
    AccountBundleDto create(CreateAccountBundle createAccountBundle);
    void deleteById(UUID id);
    PagedResult<AccountBundleDto> getAllByAccountId(UUID accountId, int page, int size);
}
