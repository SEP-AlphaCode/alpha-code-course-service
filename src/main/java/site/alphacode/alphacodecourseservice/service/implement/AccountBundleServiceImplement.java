package site.alphacode.alphacodecourseservice.service.implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateAccountBundle;
import site.alphacode.alphacodecourseservice.dto.response.AccountBundleDto;
import site.alphacode.alphacodecourseservice.dto.response.PagedResult;
import site.alphacode.alphacodecourseservice.entity.AccountBundle;
import site.alphacode.alphacodecourseservice.exception.ConflictException;
import site.alphacode.alphacodecourseservice.exception.ResourceNotFoundException;
import site.alphacode.alphacodecourseservice.mapper.AccountBundleMapper;
import site.alphacode.alphacodecourseservice.repository.AccountBundleRepository;
import site.alphacode.alphacodecourseservice.service.AccountBundleService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountBundleServiceImplement implements AccountBundleService {
    private final AccountBundleRepository accountBundleRepository;

    @Override
    @Cacheable(value = "account_bundles", key = "{#accountId, #page, #size}")
    public PagedResult<AccountBundleDto> getAllByAccountId(UUID accountId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdDate").descending());
        var accountBundles = accountBundleRepository.findAllNoneDeleteByAccountId(accountId, pageable);
        return new PagedResult<>(accountBundles.map(AccountBundleMapper::toDto));
    }

    @Override
    @Cacheable(value = "account_bundle", key = "{#accountId, #bundleId}")
    public AccountBundleDto getByAccountIdAndBundleId(UUID accountId, UUID bundleId) {
        var accountBundle = accountBundleRepository.findNoneDeleteByAccountIdAndBundleId(accountId, bundleId).orElseThrow(
                () ->
                    new ResourceNotFoundException("AccountBundle không tồn tại")
        );
        return AccountBundleMapper.toDto(accountBundle);
    }

    @Override
    @Transactional
    @CacheEvict(value = "account_bundles", allEntries = true)
    public AccountBundleDto create(CreateAccountBundle createAccountBundle) {
        var accountBundle = accountBundleRepository.findNoneDeleteByAccountIdAndBundleId(createAccountBundle.getAccountId(), createAccountBundle.getBundleId());
        if(accountBundle.isPresent()) {
            throw new ConflictException("Account bundle đã tồn tại");
        }

        var newAccountBundle = new AccountBundle();
        newAccountBundle.setAccountId(createAccountBundle.getAccountId());
        newAccountBundle.setBundleId(createAccountBundle.getBundleId());
        newAccountBundle.setStatus(1);
        accountBundleRepository.save(newAccountBundle);
        return AccountBundleMapper.toDto(newAccountBundle);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"account_bundle", "account_bundles"}, allEntries = true)
    public void deleteById(UUID id) {
        var accountBundle = accountBundleRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("AccountBundle không tồn tại")
        );
        accountBundle.setStatus(0);
        accountBundleRepository.save(accountBundle);
    }
}
