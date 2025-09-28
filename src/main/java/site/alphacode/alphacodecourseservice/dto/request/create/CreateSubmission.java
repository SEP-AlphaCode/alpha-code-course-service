package site.alphacode.alphacodecourseservice.dto.request.create;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateSubmission {
    private JsonNode logData;
    private MultipartFile videoFile; // video.mp4

    @NotNull(message = "AccountLessonId không được null")
    private UUID accountLessonId;

}
