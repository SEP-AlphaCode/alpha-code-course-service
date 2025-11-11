package site.alphacode.alphacodecourseservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountCourseEnum {
    DELETED(0, "ĐÃ XÓA"),
    IN_PROGRESS(1, "ĐANG TIẾN HÀNH"),
    COMPLETED(2, "HOÀN THÀNH"),;
    private final int code;
    private final String description;

    public static String fromCode(Integer code) {
        if (code == null) return null;
        for (AccountCourseEnum s : values()) {
            if (s.code == code) {
                return s.description;
            }
        }
        return "UNDEFINED";
    }
}
