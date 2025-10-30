package site.alphacode.alphacodecourseservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.alphacode.alphacodecourseservice.entity.Lesson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LessonRepository extends JpaRepository<Lesson, UUID> {

    // Đếm lesson active theo Section
    @Query("""
    SELECT COUNT(l)
    FROM Lesson l
    JOIN Section s ON l.sectionId = s.id
    WHERE s.courseId = :courseId
      AND l.status = 1
""")
    int countActiveLessonsByCourseId(@Param("courseId") UUID courseId);

    // Lấy lesson theo title (tránh trùng)
    @Query("SELECT l FROM Lesson l WHERE l.title = :title AND l.status <> 0")
    Optional<Lesson> findByTitle(@Param("title") String title);

    // Lấy lesson theo id
    @Query("SELECT l FROM Lesson l WHERE l.id = :id AND l.status <> 0")
    Optional<Lesson> findById(@Param("id") UUID id);

    // Lấy lesson active theo id
    @Query("SELECT l FROM Lesson l " +
            "JOIN FETCH l.section s " +
            "WHERE l.id = :lessonId AND l.status = 1")
    Optional<Lesson> findActiveWithSectionById(@Param("lessonId") UUID lessonId);

    // Lấy lesson theo slug
    @Query("SELECT l FROM Lesson l WHERE l.slug = :slug AND l.status <> 0")
    Optional<Lesson> findBySlug(@Param("slug") String slug);

    // Max order number theo Section
    @Query("SELECT COALESCE(MAX(l.orderNumber), 0) FROM Lesson l WHERE l.sectionId = :sectionId")
    Optional<Integer> findMaxOrderNumberBySectionId(@Param("sectionId") UUID sectionId);

    // Lấy tất cả lesson active theo Section
    @Query("SELECT l FROM Lesson l WHERE l.sectionId = :sectionId AND l.status = 1 ORDER BY l.orderNumber ASC")
    Page<Lesson> findAllActiveLessonsBySectionId(@Param("sectionId") UUID sectionId, Pageable pageable);

    // Lấy tất cả lesson (có solution) theo Section
    @Query("SELECT l FROM Lesson l WHERE l.sectionId = :sectionId AND l.status <> 0 ORDER BY l.orderNumber ASC")
    Page<Lesson> findAllLessonWithSolutionBySectionId(@Param("sectionId") UUID sectionId, Pageable pageable);

    // Nếu muốn lấy Lesson theo Course (join Section → Course)
    @Query("SELECT l FROM Lesson l JOIN l.section s WHERE s.course.id = :courseId AND l.status = 1 ORDER BY l.orderNumber ASC")
    Page<Lesson> findAllActiveLessonsByCourseId(@Param("courseId") UUID courseId, Pageable pageable);

    @Query("SELECT l FROM Lesson l JOIN l.section s WHERE s.course.id = :courseId AND l.status <> 0 ORDER BY l.orderNumber ASC")
    Page<Lesson> findAllLessonWithSolutionByCourseId(@Param("courseId") UUID courseId, Pageable pageable);

    // Count non-deleted lessons
    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.status <> 0")
    long countNoneDeleted();

    // Get all lessons (non-deleted) with filters and pagination for staff/admin
    @Query("""
        SELECT l FROM Lesson l
        LEFT JOIN l.section s
        WHERE l.status <> 0
          AND (:search IS NULL OR :search = ''
               OR LOWER(l.title) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(l.content) LIKE LOWER(CONCAT('%', :search, '%')))
          AND (:courseId IS NULL OR s.courseId = :courseId)
          AND (:sectionId IS NULL OR l.sectionId = :sectionId)
          AND (:type IS NULL OR l.type = :type)
          AND (:requireRobot IS NULL OR l.requireRobot = :requireRobot)
        ORDER BY l.createdDate DESC
    """)
    Page<Lesson> findAllWithFilters(
            @Param("search") String search,
            @Param("courseId") UUID courseId,
            @Param("sectionId") UUID sectionId,
            @Param("type") Integer type,
            @Param("requireRobot") Boolean requireRobot,
            Pageable pageable
    );

    @Query("SELECT l FROM Lesson l WHERE l.sectionId = :sectionId AND l.status <> 0 ORDER BY l.orderNumber ASC")
    java.util.List<Lesson> findAllNoneDeletedBySectionIdOrderByOrderNumberAsc(@Param("sectionId") UUID sectionId);

    @Query("SELECT l FROM Lesson l WHERE l.sectionId = :sectionId AND l.status = 1 ORDER BY l.orderNumber ASC")
    java.util.List<Lesson> findAllBySectionIdOrderByOrderNumberAsc(@Param("sectionId") UUID sectionId);

    // Tính tổng duration của tất cả lesson thuộc course
    @Query("SELECT SUM(l.duration) FROM Lesson l WHERE l.section.course.id = :courseId")
    Optional<Integer> sumDurationByCourseId(@Param("courseId") UUID courseId);

    // Đếm số lesson thuộc course
    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.section.course.id = :courseId")
    int countByCourseId(@Param("courseId") UUID courseId);

    Optional<Lesson> findByTitleAndSectionId(String title, UUID sectionId);
}
