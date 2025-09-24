package site.alphacode.alphacodecourseservice.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import site.alphacode.alphacodecourseservice.dto.response.AccountLessonWithLesson;
import site.alphacode.alphacodecourseservice.dto.response.AccountLessonWithDuration;
import site.alphacode.alphacodecourseservice.entity.AccountLesson;

import java.util.Optional;
import java.util.UUID;

public interface AccountLessonRepository extends JpaRepository<AccountLesson, UUID> {

    @Query("""
        SELECT new site.alphacode.alphacodecourseservice.dto.response.AccountLessonWithDuration(
            l.id, l.title, l.duration, al.status
        )
        FROM AccountLesson al
        JOIN al.lesson l
        WHERE l.course.id = :courseId AND al.accountId = :accountId
        ORDER BY l.orderNumber ASC
    """)
    Page<AccountLessonWithDuration> getLessonDurationAndTitleByCourseIdAndAccountId(
            @Param("courseId") UUID courseId,
            @Param("accountId") UUID accountId,
            Pageable pageable
    );

    Optional<AccountLessonWithLesson> findAccountLessonWithLessonById(UUID id);
}
