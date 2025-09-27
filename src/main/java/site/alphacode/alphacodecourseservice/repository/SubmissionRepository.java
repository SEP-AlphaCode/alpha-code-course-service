package site.alphacode.alphacodecourseservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.alphacode.alphacodecourseservice.entity.Submission;

import java.util.UUID;

public interface SubmissionRepository extends JpaRepository<Submission, UUID> {

}
