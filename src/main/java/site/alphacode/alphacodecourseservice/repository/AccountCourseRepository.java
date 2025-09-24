package site.alphacode.alphacodecourseservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.alphacode.alphacodecourseservice.entity.AccountCourse;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AccountCourseRepository extends JpaRepository<AccountCourse, UUID> {
    @Query("SELECT ac FROM AccountCourse ac WHERE ac.accountId = :accountId AND ac.status = 1" +
            " ORDER BY ac.purchaseDate DESC ")
    Page<AccountCourse> findByAccountId(UUID accountId, Pageable pageable);

    @Modifying
    @Query("UPDATE AccountCourse a SET a.status = 0 WHERE a.id = :id")
    void softDeleteById(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE AccountCourse a SET a.lastAccessed = :time WHERE a.id = :id")
    int updateLastAccessed(@Param("id") UUID id, @Param("time") LocalDateTime time);

    @Modifying
    @Query("UPDATE AccountCourse a SET a.lastAccessed = :time WHERE a.courseId = :courseId AND a.accountId = :accountId")
    int updateLastAccessedByAccountIdAndCourseId(@Param("courseId") UUID courseId, @Param("accountId") UUID accountId, @Param("time") LocalDateTime time);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM AccountCourse a " +
            "WHERE a.accountId = :accountId AND a.courseId = :courseId AND a.status = 1")
    boolean existsByAccountIdAndCourseId(UUID accountId, UUID courseId);
}
