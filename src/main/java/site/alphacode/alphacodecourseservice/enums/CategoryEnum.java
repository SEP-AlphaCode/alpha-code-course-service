package site.alphacode.alphacodecourseservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryEnum {
    DELETED(0, "DELETED"),
    ACTIVE(1, "ACTIVE"),
    INACTIVE(2, "INACTIVE");
    private final int code;
    private final String description;

    public static String fromCode(Integer code) {
        if (code == null) return null;
        for (CategoryEnum s : values()) {
            if (s.code == code) {
                return s.description;
            }
        }
        return "UNDEFINED";
    }
}
