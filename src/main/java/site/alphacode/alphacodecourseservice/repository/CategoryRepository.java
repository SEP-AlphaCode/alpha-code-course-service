package site.alphacode.alphacodecourseservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.alphacode.alphacodecourseservice.entity.Category;

import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    boolean existsByName(String name);

    @Modifying
    @Query("UPDATE Category c SET c.status = 0 WHERE c.id = :id")
    void softDeleteById(@Param("id") UUID id);

    @Query("""
       SELECT c FROM Category c
       WHERE c.status = 1
         AND (:searchTerm IS NULL OR :searchTerm = '' 
              OR LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
              OR LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
       ORDER BY c.createdDate DESC
       """)
    Page<Category> findAllActiveCategories(
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );

    @Query("""
       SELECT c FROM Category c
       WHERE c.status <> 0
         AND (:searchTerm IS NULL OR :searchTerm = '' 
              OR LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
              OR LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
       ORDER BY c.createdDate DESC
       """)
    Page<Category> findNoneDeleteCategories(
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );

    @Query("SELECT c FROM Category c WHERE c.id = :id AND c.status = 1")
    Optional<Category> findActiveCategoryById(@Param("id") UUID id);

    @Query("SELECT c FROM Category c WHERE c.id = :id AND c.status <> 0")
    Optional<Category> findNoneDeleteCategoryById(@Param("id") UUID id);

    @Query("SELECT c FROM Category c WHERE c.slug = :slug AND c.status = 1")
    Optional<Category> findActiveCategoryBySlug(@Param("slug") String slug);
}
