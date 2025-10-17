package site.alphacode.alphacodecourseservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CourseLevelEnum {

    BEGINNER(1, "DÀNH CHO NGƯỜI MỚI BẮT ĐẦU"),
    INTERMEDIATE(2, "TRÌNH ĐỘ TRUNG BÌNH"),
    ADVANCED(3, "TRÌNH ĐỘ NÂNG CAO");

    private final int code;
    private final String description;

    public static String fromCode(Integer code) {
        if (code == null) return null;
        for (CourseLevelEnum s : values()) {
            if (s.code == code) {
                return s.description;
            }
        }
        return "UNDEFINED";
    }
}