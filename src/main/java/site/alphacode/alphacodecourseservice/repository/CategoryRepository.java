package site.alphacode.alphacodecourseservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.alphacode.alphacodecourseservice.entity.Category;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    boolean existsByName(String name);
    @Modifying
    @Query("UPDATE Category c SET c.status = 0 WHERE c.id = :id")
    void softDeleteById(@Param("id") UUID id);

}
