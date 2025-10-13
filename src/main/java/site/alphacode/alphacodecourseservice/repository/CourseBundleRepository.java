package site.alphacode.alphacodecourseservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import site.alphacode.alphacodecourseservice.entity.Course;
import site.alphacode.alphacodecourseservice.entity.CourseBundle;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseBundleRepository extends JpaRepository<CourseBundle, UUID> {
    @Query("""
        SELECT cb.courseId FROM CourseBundle cb
        WHERE cb.bundleId = :bundleId AND cb.status = 1
    """)
    List<UUID> findCourseIdsByBundleId(@Param("bundleId") UUID bundleId);

    boolean existsByCourseIdAndBundleId(UUID courseId, UUID bundleId);
}
