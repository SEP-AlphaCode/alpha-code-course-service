package site.alphacode.alphacodecourseservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.alphacode.alphacodecourseservice.dto.response.SubmissionList;
import site.alphacode.alphacodecourseservice.entity.Submission;

import java.util.Optional;
import java.util.UUID;

public interface SubmissionRepository extends JpaRepository<Submission, UUID> {

    Optional<Submission> findTopByAccountLessonIdOrderByCreatedDateDesc(UUID accountLessonId);


    // Return a paged list of SubmissionList DTOs (joins AccountLesson and Lesson to get related data).
    @Query("""
    SELECT new site.alphacode.alphacodecourseservice.dto.response.SubmissionList(
        s.id, s.accountLessonId, al.accountId, null, l.id, l.title, s.videoUrl, s.createdDate, s.lastUpdated, s.status
    )
    FROM Submission s
    JOIN AccountLesson al ON s.accountLessonId = al.id
    JOIN Lesson l ON al.lessonId = l.id
    WHERE s.status = :status
    ORDER BY s.createdDate DESC
    """)
    Page<SubmissionList> findSubmissionsByStatus(@Param("status") Integer status, Pageable pageable);

    @Query("select s from Submission s join s.accountLesson ac left join ac.lesson where s.id = :id")
    Optional<Submission> findSubmissionById(@Param("id") UUID id);
}
