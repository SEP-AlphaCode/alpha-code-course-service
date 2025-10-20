package site.alphacode.alphacodecourseservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LessonTypeEnum {
    // Type
    LESSON(1, "BÀI HỌC"),
    VIDEO(2, "VIDEO"),
    TEST(3, "BÀI KIỂM TRA");
    private final int code;
    private final String description;

    public static String fromCode(Integer code) {
        if (code == null) return null;
        for (LessonTypeEnum s : values()) {
            if (s.code == code) {
                return s.description;
            }
        }
        return "UNDEFINED";
    }
}
