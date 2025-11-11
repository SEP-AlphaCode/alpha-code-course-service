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

import java.util.HashSet;
import java.util.Set;
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

        // Chỉ chấp nhận dạng array
        if (logData == null || !logData.isArray()) {
            submission.setStatus(4); // WAITING_FOR_REVIEW
            return false;
        }

        ArrayNode logs = (ArrayNode) logData;

        JsonNode requiredActions = getLessonSolution(submission.getAccountLessonId());
        ArrayNode missingActions = objectMapper.createArrayNode();

        Set<String> performedSet = new HashSet<>();

        for (JsonNode performed : logs) {
            if (!performed.has("type") || !performed.has("code")) continue;

            String type = performed.get("type").asText("").toLowerCase();
            String code = performed.get("code").asText("").toLowerCase();

            // code null hoặc rỗng → bỏ
            if (code == null || code.isBlank()) continue;

            performedSet.add(type + ":" + code);
        }

        for (JsonNode required : requiredActions) {
            String type = required.get("type").asText("").toLowerCase();
            String code = required.get("code").asText("").toLowerCase();
            String key = type + ":" + code;

            if (!performedSet.contains(key)) {
                missingActions.add(required);
            }
        }

        if (missingActions.isEmpty()) {
            submission.setStatus(2); // PASSED
            submission.setMissingActions(null);
            return true;
        } else {
            submission.setStatus(3); // FAILED
            submission.setMissingActions(missingActions);
            return false;
        }
    }
}
