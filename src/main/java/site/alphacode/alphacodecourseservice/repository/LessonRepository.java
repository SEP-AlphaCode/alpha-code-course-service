package site.alphacode.alphacodecourseservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.alphacode.alphacodecourseservice.entity.Lesson;

import java.util.Optional;
import java.util.UUID;

public interface LessonRepository extends JpaRepository<Lesson, UUID> {
    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.courseId = :courseId AND l.status = 1")
    int countByCourseId(@Param("courseId") UUID courseId);

    @Query("SELECT l FROM Lesson l WHERE l.title = :title AND l.status <> 0")
    Optional<Lesson> findByTitle(@Param("title") String title);

    @Query("SELECT l FROM Lesson l WHERE l.id = :id AND l.status <> 0")
    Optional<Lesson> findById(@Param("id") UUID id);

    @Query("SELECT COALESCE(MAX(l.orderNumber), 0) FROM Lesson l WHERE l.course.id = :courseId")
    Optional<Integer> findMaxOrderNumberByCourseId(@Param("courseId") UUID courseId);

    @Query("SELECT l FROM Lesson l WHERE l.course.id = :courseId AND l.status = 1 ORDER BY l.orderNumber ASC")
    Page<Lesson> findAllActiveLessonsByCourseId(@Param("courseId") UUID courseId, Pageable pageable);

    @Query("SELECT l FROM Lesson l WHERE l.course.id = :courseId AND l.status <> 0 ORDER BY l.orderNumber ASC")
    Page<Lesson> findAllLessonWithSolutionByCourseId(UUID courseId, Pageable pageable);
}
