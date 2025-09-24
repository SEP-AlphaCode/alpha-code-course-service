package site.alphacode.alphacodecourseservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import site.alphacode.alphacodecourseservice.entity.Lesson;

import java.util.UUID;

public interface LessonRepository extends JpaRepository<Lesson, UUID> {
    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.courseId = :courseId AND l.status = 1")
    int countByCourseId(UUID courseId);

}
