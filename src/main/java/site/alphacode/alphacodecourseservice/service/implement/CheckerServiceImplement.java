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
        JsonNode logData = submission.getLogData(); // robot logs

        // Nếu log chưa có, chờ chấm tay
        if (logData == null || !logData.has("logs")) {
            submission.setStatus(4); // WAITING_FOR_REVIEW
            return false;
        }

        // Lấy solution (list các {type, code})
        JsonNode requiredActions = getLessonSolution(submission.getAccountLessonId());
        ArrayNode missingActions = objectMapper.createArrayNode();

        // Tạo tập hợp các hành động đã thực hiện
        Set<String> performedSet = new HashSet<>();
        for (JsonNode performed : logData.get("logs")) {
            String key = performed.get("type").asText().toLowerCase() + ":" + performed.get("code").asText().toLowerCase();
            performedSet.add(key);
        }

        // Kiểm tra từng hành động yêu cầu
        for (JsonNode required : requiredActions) {
            String key = required.get("type").asText().toLowerCase() + ":" + required.get("code").asText().toLowerCase();
            if (!performedSet.contains(key)) {
                missingActions.add(required);
            }
        }

        // Kết quả cuối cùng
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
