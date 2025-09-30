package site.alphacode.alphacodecourseservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubmissionEnum {
    PENDING_AUTO(1, "PENDING_AUTO"),
    PASS_AUTO(2, "PASS_AUTO"),
    FAIL_AUTO(3, "FAIL_AUTO"),
    PENDING_REVIEW(4, "PENDING_REVIEW"),
    PASS_HUMAN(5, "PASS_HUMAN"),
    FAIL_HUMAN(6, "FAIL_HUMAN");

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
