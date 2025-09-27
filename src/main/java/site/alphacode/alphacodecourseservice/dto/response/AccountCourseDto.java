package site.alphacode.alphacodecourseservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import site.alphacode.alphacodecourseservice.entity.Course;
import site.alphacode.alphacodecourseservice.enums.AccountCourseEnum;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AccountCourseDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @NotNull(message = "Account ID là bắt buộc")
    private UUID accountId;

    @NotNull(message = "Course ID là bắt buộc")
    private UUID courseId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @NotNull(message = "Trạng thái hoàn thành là bắt buộc")
    private Boolean completed;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @NotNull(message = "Tổng số bài học là bắt buộc")
    @Min(value = 1, message = "Tổng số bài học phải ít nhất 1")
    private Integer totalLesson;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @NotNull(message = "Số bài học đã hoàn thành là bắt buộc")
    @Min(value = 0, message = "Bài học hoàn thành không được nhỏ hơn 0")
    private Integer completedLesson;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @NotNull(message = "Tiến độ là bắt buộc")
    @Min(value = 0, message = "Tiến độ không được nhỏ hơn 0")
    @Max(value = 100, message = "Tiến độ không được lớn hơn 100")
    private Integer progressPercent;

    @NotNull(message = "Trạng thái là bắt buộc")
    private Integer status;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @NotNull(message = "Ngày mua là bắt buộc")
    @PastOrPresent(message = "Ngày mua phải nằm trong quá khứ hoặc hiện tại")
    private LocalDateTime purchaseDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @PastOrPresent(message = "Lần truy cập cuối phải nằm trong quá khứ hoặc hiện tại")
    private LocalDateTime lastAccessed;

    @JsonProperty(value = "statusText", access = JsonProperty.Access.READ_ONLY)
    public String getStatusText() {
        return AccountCourseEnum.fromCode(this.status);
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Course course;
}
