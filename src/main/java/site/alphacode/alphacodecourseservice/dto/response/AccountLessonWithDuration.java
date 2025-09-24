package site.alphacode.alphacodecourseservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountLessonWithDuration {
    private UUID id;
    private String title;
    private Integer duration;
    private Integer status;
}
