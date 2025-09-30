package site.alphacode.alphacodecourseservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LessonEnum {
    // Status
    DELETED(0, "DELETED"),
    ACTIVE(1, "ACTIVE"),
    INACTIVE(2, "INACTIVE"),

    // Type
    LESSON(3, "LESSON"),
    TEST(4, "TEST");
    private final int code;
    private final String description;

    public static String fromCode(Integer code) {
        if (code == null) return null;
        for (LessonEnum s : values()) {
            if (s.code == code) {
                return s.description;
            }
        }
        return "UNDEFINED";
    }
}
