package site.alphacode.alphacodecourseservice.dto.request.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateSection {

    @NotBlank(message = "Tiêu đề section không được để trống")
    private String title;

    @NotNull(message = "CourseId không được để trống")
    private UUID courseId;

}
