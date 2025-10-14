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

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AccountCourseDto implements Serializable {
    private UUID id;

    private UUID accountId;

    private UUID courseId;

    private Boolean completed;

    private Integer totalLesson;

    private Integer completedLesson;

    private Integer progressPercent;

    private Integer status;

    private LocalDateTime purchaseDate;

    private LocalDateTime lastAccessed;

    private String slug;

    private String imageUrl;

    private String name;

    @JsonProperty(value = "statusText", access = JsonProperty.Access.READ_ONLY)
    public String getStatusText() {
        return AccountCourseEnum.fromCode(this.status);
    }
}
