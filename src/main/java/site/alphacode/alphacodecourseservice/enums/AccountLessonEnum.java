package site.alphacode.alphacodecourseservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountLessonEnum {
    IN_PROGRESS(1, "Đang tiến hành"),
    COMPLETED(2, "Hoàn thành");
    private final int code;
    private final String description;

    public static String fromCode(Integer code) {
        if (code == null) return null;
        for (AccountLessonEnum s : values()) {
            if (s.code == code) {
                return s.description;
            }
        }
        return "UNDEFINED";
    }
}
