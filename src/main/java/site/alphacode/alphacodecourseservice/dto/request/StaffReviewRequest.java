package site.alphacode.alphacodecourseservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StaffReviewRequest {
    @NotBlank(message = "Trạng thái duyệt là bắt buộc")
    private boolean approved;   // true = PASS, false = FAIL

    private String comment;     // Ghi chú của staff
}
