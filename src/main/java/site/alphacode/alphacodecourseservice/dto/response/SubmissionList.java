package site.alphacode.alphacodecourseservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import site.alphacode.alphacodecourseservice.base.BaseEntityDto;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class SubmissionList extends BaseEntityDto implements Serializable {
    private UUID id;
    private UUID accountLessonId;
    private UUID accountId;
    private String accountName;
    private UUID lessonId;
    private String lessonTitle;
    private String videoUrl;
}
