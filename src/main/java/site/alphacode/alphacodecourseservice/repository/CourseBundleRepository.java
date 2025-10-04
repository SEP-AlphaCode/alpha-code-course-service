package site.alphacode.alphacodecourseservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.alphacode.alphacodecourseservice.entity.CourseBundle;

import java.util.List;
import java.util.UUID;

public interface CourseBundleRepository extends JpaRepository<CourseBundle, UUID> {
    @Query("SELECT cb FROM CourseBundle cb WHERE cb.bundleId = :courseBundleId and cb.status = 1")
    List<CourseBundle> findByBundleId(@Param("bundleId") UUID bundleId);

    List<UUID> findCourseIdsByBundleId(UUID bundleId);
}
