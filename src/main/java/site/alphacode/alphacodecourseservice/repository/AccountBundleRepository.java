package site.alphacode.alphacodecourseservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.alphacode.alphacodecourseservice.entity.AccountBundle;

import java.util.Optional;
import java.util.UUID;

public interface AccountBundleRepository extends JpaRepository<AccountBundle, UUID> {

    @Query("SELECT ab FROM AccountBundle ab WHERE ab.accountId = :accountId AND ab.bundleId = :bundleId AND ab.status <> 0")
    Optional<AccountBundle> findNoneDeleteByAccountIdAndBundleId(@Param("accountId") UUID accountId,@Param("bundleId") UUID bundleId);

    @Query("SELECT ab FROM AccountBundle ab WHERE ab.accountId = :accountId AND ab.status <> 0")
    Page<AccountBundle> findAllNoneDeleteByAccountId(@Param("accountId") UUID accountId, Pageable pageable);
}
