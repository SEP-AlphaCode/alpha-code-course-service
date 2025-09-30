package site.alphacode.alphacodecourseservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubmissionEnum {
    PENDING_AUTO(1, "CHỜ CHẤM TỰ ĐỘNG"),
    PASS_AUTO(2, "CHẤM ĐẠT TỰ ĐỘNG"),
    FAIL_AUTO(3, "CHẤM KHÔNG ĐẠT TỰ ĐỘNG"),
    PENDING_REVIEW(4, "CHỜ CHẤM BỞI NHÂN VIÊN"),
    PASS_HUMAN(5, "CHẤM ĐẠT BỞI NHÂN VIÊN"),
    FAIL_HUMAN(6, "CHẤM KHÔNG ĐẠT BỞI NHÂN VIÊN");

    private final int code;
    private final String description;

    public static String fromCode(Integer code) {
        if (code == null) return null;
        for (SubmissionEnum s : values()) {
            if (s.code == code) {
                return s.description;
            }
        }
        return "UNDEFINED";
    }
}
