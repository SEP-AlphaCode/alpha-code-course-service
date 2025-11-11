package site.alphacode.alphacodecourseservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.alphacode.alphacodecourseservice.dto.response.AccountLessonWithDuration;
import site.alphacode.alphacodecourseservice.entity.AccountLesson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountLessonRepository extends JpaRepository<AccountLesson, UUID> {

    @Query("""
    SELECT new site.alphacode.alphacodecourseservice.dto.response.AccountLessonWithDuration(
        l.id, l.title, l.slug, l.duration, al.status
    )
    FROM AccountLesson al
    JOIN Lesson l ON al.lessonId = l.id
    JOIN Section s ON l.sectionId = s.id
    WHERE s.courseId = :courseId AND al.accountId = :accountId
    ORDER BY l.orderNumber ASC
""")
    Page<AccountLessonWithDuration> getLessonDurationAndTitleByCourseIdAndAccountId(
            @Param("courseId") UUID courseId,
            @Param("accountId") UUID accountId,
            Pageable pageable
    );

    @Query("SELECT al FROM AccountLesson al JOIN al.lesson l JOIN l.section s WHERE al.accountId = :accountId AND s.course.id = :courseId")
    List<AccountLesson> findAllByAccountIdAndCourseId(@Param("accountId") UUID accountId, @Param("courseId") UUID courseId);

    Optional<AccountLesson> findByAccountIdAndLessonId(UUID accountId, UUID lessonId);

    @Query("SELECT COUNT(al) FROM AccountLesson al WHERE al.accountId = :accountId AND al.status = :status")
    Long countByAccountIdAndStatus(@Param("accountId") UUID accountId, @Param("status") Integer status);

    @Query("""
        SELECT new site.alphacode.alphacodecourseservice.dto.response.RecentActivity(
            c.name, l.title, al.lastUpdated
        )
        FROM AccountLesson al
        JOIN Lesson l ON al.lessonId = l.id
        JOIN Section s ON l.sectionId = s.id
        JOIN Course c ON s.courseId = c.id
        WHERE al.accountId = :accountId
        AND al.status = 2
        AND al.lastUpdated IS NOT NULL
        ORDER BY al.lastUpdated DESC
        LIMIT :limit
    """)
    List<site.alphacode.alphacodecourseservice.dto.response.RecentActivity> findRecentCompletedActivities(
            @Param("accountId") UUID accountId, 
            @Param("limit") int limit
    );

    void deleteAllByLessonId(UUID lessonId);

    @Query("""
        SELECT al FROM AccountLesson al
        JOIN Lesson l ON al.lessonId = l.id
        JOIN Section s ON l.sectionId = s.id
        WHERE s.courseId = :courseId
    """)
    List<AccountLesson> findAllByCourseId(@Param("courseId") UUID courseId);

    @Query("""
        UPDATE AccountLesson al
        SET al.status = 0, al.completedAt = null
        WHERE al.accountId = :accountId
        AND al.lessonId IN (
            SELECT l.id FROM Lesson l
            JOIN Section s ON l.sectionId = s.id
            WHERE s.courseId = :courseId
        )
    """)
    @org.springframework.data.jpa.repository.Modifying
    void softDeleteByAccountIdAndCourseId(@Param("accountId") UUID accountId, @Param("courseId") UUID courseId);
}
