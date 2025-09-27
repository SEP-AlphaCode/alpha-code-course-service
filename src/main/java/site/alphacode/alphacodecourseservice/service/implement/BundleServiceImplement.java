package site.alphacode.alphacodecourseservice.service.implement;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateBundle;
import site.alphacode.alphacodecourseservice.dto.request.patch.PatchBundle;
import site.alphacode.alphacodecourseservice.dto.request.update.UpdateBundle;
import site.alphacode.alphacodecourseservice.dto.response.BundleDto;
import site.alphacode.alphacodecourseservice.dto.response.PagedResult;
import site.alphacode.alphacodecourseservice.entity.Bundle;
import site.alphacode.alphacodecourseservice.exception.BadRequestException;
import site.alphacode.alphacodecourseservice.exception.ConflictException;
import site.alphacode.alphacodecourseservice.mapper.BundleMapper;
import site.alphacode.alphacodecourseservice.repository.BundleRepository;
import site.alphacode.alphacodecourseservice.service.BundleService;
import site.alphacode.alphacodecourseservice.service.S3Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BundleServiceImplement implements BundleService {
    private final BundleRepository bundleRepository;
    private final S3Service s3Service;

    @Override
    @Cacheable(value = "bundle", key = "#id")
    public BundleDto getById(UUID id){
        var bundle = bundleRepository.findNoneDeleteById(id).orElseThrow(
                () -> new BadRequestException("Gói không tồn tại")
        );
        return BundleMapper.toDto(bundle);
    }

    @Override
    @Cacheable(value = "active_bundle", key = "#id")
    public BundleDto getActiveById(UUID id){
        var bundle = bundleRepository.findByIdAndStatus(id, 1).orElseThrow(
                () -> new BadRequestException("Gói không tồn tại")
        );
        return BundleMapper.toDto(bundle);
    }

    @Override
    @Cacheable(value = "bundles_list", key = "{#page, #size, #search}")
    public PagedResult<BundleDto> getAllActiveBundles(Integer page, Integer size, String search){
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdDate").descending());
        Page<Bundle> bundlesPage = bundleRepository.findAllActiveBundles(search, pageable);
        return new PagedResult<>(bundlesPage.map(BundleMapper::toDto));
    }

    @Override
    @Cacheable(value = "all_bundles_list", key = "{#page, #size, #search}")
    public PagedResult<BundleDto> getNoneDeleteBundles(Integer page, Integer size, String search){
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdDate").descending());
        Page<Bundle> bundlesPage = bundleRepository.findNoneDeleteBundles(search, pageable);
        return new PagedResult<>(bundlesPage.map(BundleMapper::toDto));
    }

    @Override
    @Transactional
    @CachePut(value = "bundle", key = "#result.id")
    @Caching(
            evict = {
                    @CacheEvict(value = "bundles_list", allEntries = true),
                    @CacheEvict(value = "all_bundles_list", allEntries = true)
            }
    )
    public BundleDto create(CreateBundle createBundle) {
        var bundle = bundleRepository.findByName(createBundle.getName());
        if (bundle.isPresent()) {
            throw new ConflictException("Gói với tên: " + createBundle.getName() + " đã tồn tại");
        }

        if (createBundle.getDiscountPrice() != null) {
            if (createBundle.getDiscountPrice().compareTo(createBundle.getPrice()) >= 0) {
                throw new BadRequestException("Giá khuyến mãi phải nhỏ hơn giá gốc");
            }
        }

        var newBundle = new Bundle();
        newBundle.setName(createBundle.getName());
        newBundle.setDescription(createBundle.getDescription());
        newBundle.setPrice(createBundle.getPrice());
        newBundle.setDiscountPrice(createBundle.getDiscountPrice());
        newBundle.setCreatedDate(LocalDateTime.now());
        newBundle.setLastUpdated(null);
        newBundle.setStatus(1);

        try {
            if (createBundle.getCoverImage() != null && !createBundle.getCoverImage().isEmpty()) {
                String fileKey = "bundles/" + createBundle.getCoverImage().getOriginalFilename();
                String imageUrl = s3Service.uploadBytes(createBundle.getCoverImage().getBytes(), fileKey, createBundle.getCoverImage().getContentType());
                newBundle.setCoverImage(imageUrl);
            }

            Bundle savedEntity = bundleRepository.save(newBundle);
            return BundleMapper.toDto(savedEntity);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo Category", e);
        }
    }

    @Override
    @Transactional
    @CachePut(value = "bundle", key = "#id")
    @Caching(
            evict = {
                    @CacheEvict(value = "bundles_list", allEntries = true),
                    @CacheEvict(value = "all_bundles_list", allEntries = true),
                    @CacheEvict(value = "active_bundle", key = "#id")
            }
    )
    public BundleDto update(UUID id, UpdateBundle updateBundle){
        var existing = bundleRepository.findNoneDeleteById(id).orElseThrow(
                () -> new BadRequestException("Gói không tồn tại")
        );

        if (!existing.getName().equals(updateBundle.getName())) {
            var bundleWithName = bundleRepository.findByName(updateBundle.getName());
            if (bundleWithName.isPresent() && !bundleWithName.get().getId().equals(updateBundle.getId())) {
                throw new ConflictException("Gói với tên: " + updateBundle.getName() + " đã tồn tại");
            }
        }

        if (updateBundle.getDiscountPrice() != null) {
            if (updateBundle.getDiscountPrice().compareTo(updateBundle.getPrice()) >= 0) {
                throw new BadRequestException("Giá khuyến mãi phải nhỏ hơn giá gốc");
            }
        }

        existing.setName(updateBundle.getName());
        existing.setDescription(updateBundle.getDescription());
        existing.setPrice(updateBundle.getPrice());
        existing.setDiscountPrice(updateBundle.getDiscountPrice());
        existing.setStatus(updateBundle.getStatus());
        existing.setLastUpdated(LocalDateTime.now());

        // Xử lý ảnh (PUT => bắt buộc có image file hoặc imageUrl)
        if (updateBundle.getImage() != null && !updateBundle.getImage().isEmpty()) {
            try {
                String fileKey = "bundles/" + updateBundle.getImage().getOriginalFilename();
                String imageUrl = s3Service.uploadBytes(
                        updateBundle.getImage().getBytes(),
                        fileKey,
                        updateBundle.getImage().getContentType()
                );
                existing.setCoverImage(imageUrl);
            } catch (Exception e) {
                throw new RuntimeException("Lỗi khi tải lên ảnh", e);
            }
        } else if (updateBundle.getCoverImage() != null && !updateBundle.getCoverImage().isBlank()) {
            existing.setCoverImage(updateBundle.getCoverImage());
        } else {
            throw new BadRequestException("Chỉnh sửa gói phải có ảnh (image file hoặc imageUrl)");
        }

        Bundle savedEntity = bundleRepository.save(existing);
        return BundleMapper.toDto(savedEntity);
    }

    @Override
    @Transactional
    @CachePut(value = "bundle", key = "#id")
    @Caching(
            evict = {
                    @CacheEvict(value = "bundles_list", allEntries = true),
                    @CacheEvict(value = "all_bundles_list", allEntries = true),
                    @CacheEvict(value = "active_bundle", key = "#id")
            }
    )
    public BundleDto patch(UUID id, PatchBundle patchBundle){
        var existing = bundleRepository.findNoneDeleteById(id).orElseThrow(
                () -> new BadRequestException("Gói không tồn tại")
        );

        if (patchBundle.getName() != null && !existing.getName().equals(patchBundle.getName())) {
            var bundleWithName = bundleRepository.findByName(patchBundle.getName());
            if (bundleWithName.isPresent() && !bundleWithName.get().getId().equals(existing.getId())) {
                throw new ConflictException("Gói với tên: " + patchBundle.getName() + " đã tồn tại");
            }
            existing.setName(patchBundle.getName());
        }

        if (patchBundle.getPrice() != null) {
            existing.setPrice(patchBundle.getPrice());
            if (existing.getDiscountPrice() != null) {
                if (existing.getDiscountPrice().compareTo(existing.getPrice()) >= 0) {
                    throw new BadRequestException("Giá khuyến mãi phải nhỏ hơn giá gốc");
                }
            }
        }

        if (patchBundle.getDiscountPrice() != null) {
            if (patchBundle.getDiscountPrice().compareTo(existing.getPrice()) >= 0) {
                throw new BadRequestException("Giá khuyến mãi phải nhỏ hơn giá gốc");
            }
            existing.setDiscountPrice(patchBundle.getDiscountPrice());
        }

        if (patchBundle.getDescription() != null) {
            existing.setDescription(patchBundle.getDescription());
        }

        if (patchBundle.getStatus() != null) {
            existing.setStatus(patchBundle.getStatus());
        }

        existing.setLastUpdated(LocalDateTime.now());

        // Xử lý ảnh (PATCH => có thể không có image file và imageUrl)
        if (patchBundle.getImage() != null && !patchBundle.getImage().isEmpty()) {
            try {
                String fileKey = "bundles/" + patchBundle.getImage().getOriginalFilename();
                String imageUrl = s3Service.uploadBytes(
                        patchBundle.getImage().getBytes(),
                        fileKey,
                        patchBundle.getImage().getContentType()
                );
                existing.setCoverImage(imageUrl);
            } catch (Exception e) {
                throw new RuntimeException("Lỗi khi tải lên ảnh", e);
            }
        } else if (patchBundle.getCoverImage() != null && !patchBundle.getCoverImage().isBlank()) {
            existing.setCoverImage(patchBundle.getCoverImage());
        }
        Bundle savedEntity = bundleRepository.save(existing);
        return BundleMapper.toDto(savedEntity);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"bundle", "active_bundle"}, key = "#id")
    @Caching(
            evict = {
                    @CacheEvict(value = "bundles_list", allEntries = true),
                    @CacheEvict(value = "all_bundles_list", allEntries = true)
            }
    )
    public void delete(UUID id){
        var existing = bundleRepository.findNoneDeleteById(id).orElseThrow(
                () -> new BadRequestException("Gói không tồn tại")
        );
        existing.setStatus(0);
        existing.setLastUpdated(LocalDateTime.now());
        bundleRepository.save(existing);
    }
}
