package site.alphacode.alphacodecourseservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.alphacode.alphacodecourseservice.entity.Submission;
import java.util.Optional;
import java.util.UUID;

public interface SubmissionRepository extends JpaRepository<Submission, UUID> {

    Optional<Submission> findTopByAccountLessonIdAndStatusOrderByCreatedDateDesc(UUID accountLessonId, int status);


}
