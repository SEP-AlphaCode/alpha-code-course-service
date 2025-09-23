package site.alphacode.alphacodecourseservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import site.alphacode.alphacodecourseservice.baseentity.BaseEntity;

import java.util.UUID;

@Entity
@Table(name = "lesson")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Lesson extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @NotBlank(message = "Tiêu đề bài học là bắt buộc")
    @Size(max = 255, message = "Tiêu đề bài học không được vượt quá 255 ký tự")
    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Size(max = 500, message = "Đường dẫn nội dung không được vượt quá 500 ký tự")
    @Column(name = "content_url", length = 500)
    private String contentUrl;

    @NotBlank(message = "Loại nội dung là bắt buộc")
    @Size(max = 100, message = "Loại nội dung không được vượt quá 100 ký tự")
    @Column(name = "content_type", length = 100, nullable = false)
    private String contentType;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @NotNull(message = "Thời lượng bài học là bắt buộc")
    @Min(value = 1, message = "Thời lượng bài học phải ít nhất 1 giây")
    @Column(name = "duration", nullable = false)
    private Integer duration; // tính bằng giây

    @NotNull(message = "Trạng thái yêu cầu robot là bắt buộc")
    @Column(name = "require_robot", nullable = false)
    private Boolean requireRobot = false;

    @NotNull(message = "Course ID là bắt buộc")
    @Column(name = "course_id", nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID courseId;
}

