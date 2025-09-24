package site.alphacode.alphacodecourseservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.alphacode.alphacodecourseservice.base.BaseEntityDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmissionDto extends BaseEntityDto {
    private String id;
    private String logFileUrl;
    private String videoUrl;

    @NotBlank(message = "Account Lesson ID không được để trống")
    private String accountLessonId;
}

