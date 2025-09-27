package site.alphacode.alphacodecourseservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import site.alphacode.alphacodecourseservice.base.BaseEntityDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class SubmissionDto extends BaseEntityDto {
    private String id;
    private String logFileUrl;
    private String videoUrl;
    private String accountLessonId;
}

