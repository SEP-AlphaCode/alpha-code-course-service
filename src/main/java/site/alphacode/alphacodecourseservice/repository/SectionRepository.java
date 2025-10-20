package site.alphacode.alphacodecourseservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.alphacode.alphacodecourseservice.entity.Section;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SectionRepository extends JpaRepository<Section, UUID> {

    @Query("SELECT s FROM Section s WHERE s.courseId = :courseId and s.status <> 0 ORDER BY s.orderNumber ASC")
    List<Section> findAllByCourseId(@Param("courseId") UUID courseId);

    @Query("SELECT s FROM Section s WHERE s.id = :id")
    Optional<Section> findById(@Param("id") UUID id);

    @Query("SELECT s FROM Section s WHERE s.title = :title AND s.courseId = :courseId")
    Optional<Section> findByTitleAndCourseId(@Param("title") String title, @Param("courseId") UUID courseId);

    @Query("SELECT MAX(s.orderNumber) FROM Section s WHERE s.courseId = :courseId")
    Integer findMaxOrderNumberByCourseId(@Param("courseId") UUID courseId);

    // Count non-deleted sections
    @Query("SELECT COUNT(s) FROM Section s WHERE s.status <> 0")
    long countNoneDeleted();

    // Count active (non-deleted) sections by course id
    @Query("SELECT COUNT(s) FROM Section s WHERE s.courseId = :courseId AND s.status <> 0")
    long countActiveByCourseId(@Param("courseId") UUID courseId);
}
