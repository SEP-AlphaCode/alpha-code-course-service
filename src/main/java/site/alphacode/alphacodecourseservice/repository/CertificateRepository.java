package site.alphacode.alphacodecourseservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.alphacode.alphacodecourseservice.entity.Certificate;

import java.util.Optional;
import java.util.UUID;

public interface CertificateRepository extends JpaRepository<Certificate, UUID> {

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Certificate c WHERE c.accountId = :accountId AND c.courseId = :courseId AND c.status = 1")
    boolean existsByAccountIdAndCourseId(UUID accountId, UUID courseId);

    @Query("SELECT c FROM Certificate c WHERE c.accountId = :accountId AND c.courseId = :courseId AND c.status = 1")
    Optional<Certificate> getByAccountIdAndCourseId(@Param("accountId") UUID accountId,@Param("courseId") UUID courseId);
}
