package site.alphacode.alphacodecourseservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubmissionEnum {
    SUBMITTED(1, "STARTED"),
    COMPLETED(2, "COMPLETED"),
    FAILED(3, "FAILED");
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
