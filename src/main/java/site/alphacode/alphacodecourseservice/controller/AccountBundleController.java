package site.alphacode.alphacodecourseservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import site.alphacode.alphacodecourseservice.dto.response.AccountBundleDto;
import site.alphacode.alphacodecourseservice.dto.response.PagedResult;
import site.alphacode.alphacodecourseservice.service.AccountBundleService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/account-bundles")
@RequiredArgsConstructor
@Tag(name = "Account Bundles", description = "Account Bundle management APIs")
public class AccountBundleController {
    private final AccountBundleService accountBundleService;

    @GetMapping()
    @Operation(summary = "Get account bundle by account id and bundle id")
    public AccountBundleDto getByAccountIdAndBundleId(@RequestParam UUID accountId,@RequestParam UUID bundleId) {
        return accountBundleService.getByAccountIdAndBundleId(accountId, bundleId);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete account bundle by id")
    public void deleteById(@PathVariable UUID id) {
        accountBundleService.deleteById(id);
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "Get account bundle by account id")
    public PagedResult<AccountBundleDto> getByAccountId(@PathVariable UUID accountId,
                                                     @RequestParam(value = "page", defaultValue = "1") int page,
                                                     @RequestParam(value = "size", defaultValue = "10") int size) {
        return accountBundleService.getAllByAccountId(accountId, page, size);
    }
}
