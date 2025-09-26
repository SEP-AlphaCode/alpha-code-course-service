package site.alphacode.alphacodecourseservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.alphacode.alphacodecourseservice.entity.Category;
import site.alphacode.alphacodecourseservice.entity.Course;

import java.util.Optional;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
    @Query("""
       SELECT c FROM Course c
       WHERE c.status == 1
         AND (:searchTerm IS NULL OR :searchTerm = '' 
              OR LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
              OR LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
       ORDER BY c.createdDate DESC
       """)
    Page<Course> findAllActiveCourse(
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );

    @Query("SELECT c FROM Course c WHERE c.status <> 0")
    boolean existsByName(String name);

    Optional<Course> findActiveCourseById(UUID id);

    @Query("SELECT c FROM Course c WHERE c.slug = :slug AND c.status == 1")
    Optional<Course> findActiveCourseBySlug(String slug);

    @Query("SELECT c FROM Course c WHERE c.id = :id AND c.status <> 0")
    Optional<Course>  findNoneDeleteCourseById(UUID id);
}
