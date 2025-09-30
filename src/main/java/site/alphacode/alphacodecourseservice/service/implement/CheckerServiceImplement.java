package site.alphacode.alphacodecourseservice.service.implement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.alphacode.alphacodecourseservice.entity.Submission;
import site.alphacode.alphacodecourseservice.repository.AccountLessonRepository;
import site.alphacode.alphacodecourseservice.repository.LessonRepository;
import site.alphacode.alphacodecourseservice.service.CheckerService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckerServiceImplement implements CheckerService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final LessonRepository lessonRepository;
    private final AccountLessonRepository accountLessonRepository;

    private JsonNode getLessonSolution(UUID accountLessonId) {
        var accountLesson = accountLessonRepository.findById(accountLessonId)
                .orElseThrow(() -> new RuntimeException("AccountLesson với id: " + accountLessonId + " không tìm thấy"));
        var lesson = lessonRepository.findById(accountLesson.getLessonId())
                .orElseThrow(() -> new RuntimeException("Lesson với id: " + accountLesson.getLessonId() + " không tìm thấy"));
        return lesson.getSolution();
    }

    public boolean autoCheck(Submission submission) {
        JsonNode logData = submission.getLogData();

        if (logData == null) {
            submission.setStatus(4); // WAITING_FOR_REVIEW
            return false;
        }

        JsonNode requiredActions = getLessonSolution(submission.getAccountLessonId());
        ArrayNode missingActions = objectMapper.createArrayNode();

        for (JsonNode required : requiredActions) {
            boolean found = false;
            for (JsonNode performed : logData.get("actions")) {
                if (required.get("action").asText().equals(performed.get("action").asText())
                        && required.get("params").equals(performed.get("params"))) {
                    found = true;
                    break;
                }
            }
            if (!found) missingActions.add(required);
        }

        if (missingActions.size() == 0) {
            submission.setStatus(2); // PASSED
            submission.setMissingActions(null);
            return true; // báo cho service biết là pass
        } else {
            submission.setStatus(3); // FAILED
            submission.setMissingActions(missingActions);
            return false;
        }
    }

}
