package site.alphacode.alphacodecourseservice.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import site.alphacode.alphacodecourseservice.entity.Bundle;
import site.alphacode.alphacodecourseservice.entity.Category;

import java.util.Optional;
import java.util.UUID;

public interface BundleRepository extends JpaRepository<Bundle, UUID> {

    @Query("SELECT b FROM Bundle b WHERE b.name = :name AND b.status <> 0")
    Optional<Bundle> findByName(@Param("name") String name);

    @Query("SELECT b FROM Bundle b WHERE b.id = :id AND b.status <> 0")
    Optional<Bundle> findNoneDeleteById(@Param("id") UUID id);

    @Query("SELECT b FROM Bundle b WHERE b.id = :id AND b.status = :status")
    Optional<Bundle> findByIdAndStatus(@Param("id") UUID id, @Param("status") Integer status);

    @Query("""
       SELECT b FROM Bundle b
       WHERE b.status = 1
         AND (:searchTerm IS NULL OR :searchTerm = '' 
              OR LOWER(b.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
              OR LOWER(b.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
       ORDER BY b.createdDate DESC
       """)
    Page<Bundle> findAllActiveBundles(@Param("searchTerm") String searchTerm,
            Pageable pageable
    );

    @Query("""
       SELECT b FROM Bundle b
       WHERE b.status <> 0
         AND (:searchTerm IS NULL OR :searchTerm = '' 
              OR LOWER(b.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
              OR LOWER(b.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
       ORDER BY b.createdDate DESC
       """)
    Page<Bundle> findNoneDeleteBundles(@Param("searchTerm") String searchTerm,
                                      Pageable pageable
    );
}
