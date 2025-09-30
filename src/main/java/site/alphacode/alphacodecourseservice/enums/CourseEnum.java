package site.alphacode.alphacodecourseservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CourseEnum {

    DELETED(0, "ĐÃ XÓA"),
    ACTIVE(1, "ĐANG HOẠT ĐỘNG"),
    INACTIVE(2, "KHÔNG HOẠT ĐỘNG"),
    BEGINNER(3, "DÀNH CHO NGƯỜI MỚI BẮT ĐẦU"),
    INTERMEDIATE(4, "TRÌNH ĐỘ TRUNG BÌNH"),
    ADVANCED(5, "TRÌNH ĐỘ NÂNG CAO");
    private final int code;
    private final String description;

    public static String fromCode(Integer code) {
        if (code == null) return null;
        for (CourseEnum s : values()) {
            if (s.code == code) {
                return s.description;
            }
        }
        return "UNDEFINED";
    }
}