package site.alphacode.alphacodecourseservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.alphacode.alphacodecourseservice.entity.Course;

import java.util.Optional;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
    @Query("""
       SELECT c FROM Course c
       WHERE c.status = 1
         AND (:categoryId IS NULL OR c.category.id = :categoryId)
         AND (:searchTerm IS NULL OR :searchTerm = '' 
              OR LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
              OR LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
       ORDER BY c.createdDate DESC
       """)
    Page<Course> findAllActiveCourse(
            @Param("searchTerm") String searchTerm,@Param("categoryId") UUID categoryId,
            Pageable pageable
    );

    @Query("""
       SELECT c FROM Course c
       WHERE c.status <> 0
         AND (:searchTerm IS NULL OR :searchTerm = '' 
              OR LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
              OR LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
       ORDER BY c.createdDate DESC
       """)
    Page<Course> findNoneDeleteCourses(
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Course c WHERE c.name = :name AND c.status <> 0")
    Boolean existsByName(@Param("name") String name);

    @Query("SELECT c FROM Course c WHERE c.id = :id AND c.status = 1")
    Optional<Course> findActiveCourseById(@Param("id") UUID id);

    @Query("SELECT c FROM Course c WHERE c.slug = :slug AND c.status <> 0")
    Optional<Course> findCourseBySlug(@Param("slug") String slug);

    @Query("SELECT c FROM Course c WHERE c.id = :id AND c.status <> 0")
    Optional<Course>  findNoneDeleteCourseById(@Param("id") UUID id);

    // Count non-deleted courses
    @Query("SELECT COUNT(c) FROM Course c WHERE c.status <> 0")
    long countNoneDeleted();

    @Query("SELECT COUNT(c) FROM Course c WHERE c.category.id = :categoryId AND c.status <> 0")
    int countCoursesByCategoryId(@Param("categoryId") UUID categoryId);

    Optional<Course> findBySlug(String courseSlug);
}

