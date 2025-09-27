package site.alphacode.alphacodecourseservice.service;

import site.alphacode.alphacodecourseservice.dto.request.create.CreateBundle;
import site.alphacode.alphacodecourseservice.dto.request.patch.PatchBundle;
import site.alphacode.alphacodecourseservice.dto.request.update.UpdateBundle;
import site.alphacode.alphacodecourseservice.dto.response.BundleDto;
import site.alphacode.alphacodecourseservice.dto.response.PagedResult;

import java.util.UUID;

public interface BundleService {
    PagedResult<BundleDto> getNoneDeleteBundles(Integer page, Integer size, String search);
    BundleDto getById(UUID id);
    BundleDto getActiveById(UUID id);
    PagedResult<BundleDto> getAllActiveBundles(Integer page, Integer size, String search);
    BundleDto create(CreateBundle createBundle);
    BundleDto update(UUID id, UpdateBundle updateBundle);
    BundleDto patch(UUID id, PatchBundle patchBundle);
    void delete(UUID id);
}
