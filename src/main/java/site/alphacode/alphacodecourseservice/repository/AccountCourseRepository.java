package site.alphacode.alphacodecourseservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.alphacode.alphacodecourseservice.entity.AccountCourse;
import site.alphacode.alphacodecourseservice.entity.Course;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountCourseRepository extends JpaRepository<AccountCourse, UUID> {
    @Query("""
    SELECT ac FROM AccountCourse ac
    JOIN FETCH ac.course
    WHERE ac.accountId = :accountId
      AND ac.status = 1
    ORDER BY ac.purchaseDate DESC
""")
    Page<AccountCourse> findActiveByAccountId(@Param("accountId") UUID accountId, Pageable pageable);

    @Modifying
    @Query("UPDATE AccountCourse a SET a.status = 0 WHERE a.id = :id")
    void softDeleteById(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE AccountCourse a SET a.lastAccessed = :time WHERE a.id = :id")
    void updateLastAccessed(@Param("id") UUID id, @Param("time") LocalDateTime time);

    @Modifying
    @Query("UPDATE AccountCourse a SET a.lastAccessed = :time WHERE a.courseId = :courseId AND a.accountId = :accountId")
    void updateLastAccessedByAccountIdAndCourseId(@Param("courseId") UUID courseId, @Param("accountId") UUID accountId, @Param("time") LocalDateTime time);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM AccountCourse a " +
            "WHERE a.accountId = :accountId AND a.courseId = :courseId AND a.status = 1")
    boolean existsByAccountIdAndCourseId(UUID accountId, UUID courseId);

    @Query("SELECT ac FROM AccountCourse ac join ac.course WHERE ac.accountId = :accountId AND ac.courseId = :courseId AND ac.status = 1")
    Optional<AccountCourse> findByAccountIdAndCourseId(@Param("accountId") UUID accountId, @Param("courseId") UUID courseId);

    @Query("SELECT ac.courseId FROM AccountCourse ac " +
            "WHERE ac.accountId = :accountId AND ac.courseId IN :courseIds AND ac.status = 1")
    List<UUID> findOwnedCourseIds(UUID accountId, List<UUID> courseIds);

    @Query("SELECT COUNT(ac) FROM AccountCourse ac WHERE ac.accountId = :accountId AND ac.status = 1")
    Long countByAccountId(@Param("accountId") UUID accountId);

    @Query("SELECT COUNT(ac) FROM AccountCourse ac WHERE ac.accountId = :accountId AND ac.status = :status")
    Long countByAccountIdAndStatus(@Param("accountId") UUID accountId, @Param("status") Integer status);


    @Query("""
    SELECT ac FROM AccountCourse ac
    WHERE ac.accountId = :accountId
    ORDER BY ac.purchaseDate DESC
    """)
    List<AccountCourse> findAccountCourseByAccountId(@Param("accountId") UUID accountId, Pageable pageable);

    @Query("SELECT ac FROM AccountCourse ac WHERE ac.courseId = :courseId")
    List<AccountCourse> findAllByCourseId(@Param("courseId") UUID courseId);

}
