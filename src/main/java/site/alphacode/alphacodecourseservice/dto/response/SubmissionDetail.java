package site.alphacode.alphacodecourseservice.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
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
public class SubmissionDetail extends BaseEntityDto implements Serializable {
    private UUID id;

    private UUID accountLessonId;

    private UUID accountId;

    private String accountName;

    private UUID lessonId;

    private String lessonTitle;

    private JsonNode logData;

    private String videoUrl;

    private String staffComment;

    private JsonNode missingActions;
}
