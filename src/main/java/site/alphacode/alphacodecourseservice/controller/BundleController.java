package site.alphacode.alphacodecourseservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateBundle;
import site.alphacode.alphacodecourseservice.dto.request.patch.PatchBundle;
import site.alphacode.alphacodecourseservice.dto.request.update.UpdateBundle;
import site.alphacode.alphacodecourseservice.dto.response.BundleDto;
import site.alphacode.alphacodecourseservice.dto.response.PagedResult;
import site.alphacode.alphacodecourseservice.service.BundleService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bundles")
@RequiredArgsConstructor
@Tag(name = "Bundles", description = "Bundle management APIs")
public class BundleController {
    private final BundleService bundleService;

    @GetMapping("/{id}")
    @Operation(summary = "Get bundle by id")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public BundleDto getBundleById(@PathVariable UUID id) {
        return bundleService.getById(id);
    }

    @GetMapping("/active/{id}")
    @Operation(summary = "Get active bundle by id")
    public BundleDto getActiveBundleById(@PathVariable UUID id) {
        return bundleService.getActiveById(id);
    }

    @PostMapping
    @Operation(summary = "Create new bundle")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public BundleDto createBundle(@RequestBody CreateBundle createBundle) {
        return bundleService.create(createBundle);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update bundle by id")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public BundleDto updateBundle(@PathVariable UUID id, @RequestBody UpdateBundle updateBundle) {
        return bundleService.update(id, updateBundle);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Patch bundle by id")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public BundleDto patchBundle(@PathVariable UUID id, @RequestBody PatchBundle patchBundle) {
        return bundleService.patch(id, patchBundle);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete bundle by id")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public void deleteBundle(@PathVariable UUID id) {
        bundleService.delete(id);
    }

    @GetMapping()
    @Operation(summary = "Get all active bundles with pagination and optional search")
    public PagedResult<BundleDto> getAllActiveBundles(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search) {
        return bundleService.getAllActiveBundles(page, size, search);
    }

    @GetMapping("/get-none-delete")
    @Operation(summary = "Get all none delete bundles with pagination and optional search")
    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Staff')")
    public PagedResult<BundleDto> getNoneDeleteBundles(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search) {
        return bundleService.getNoneDeleteBundles(page, size, search);
    }
}
