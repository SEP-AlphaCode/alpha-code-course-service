package site.alphacode.alphacodecourseservice.util;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;

public class SlugHelper {

    private static final Map<Character, String> VIETNAMESE_MAP = new HashMap<>();
    static {
        // Chuyển các ký tự đặc biệt sang ASCII tương ứng
        VIETNAMESE_MAP.put('đ', "d");
        VIETNAMESE_MAP.put('Đ', "D");
        VIETNAMESE_MAP.put('á', "a"); VIETNAMESE_MAP.put('à', "a"); VIETNAMESE_MAP.put('ả', "a"); VIETNAMESE_MAP.put('ã', "a"); VIETNAMESE_MAP.put('ạ', "a");
        VIETNAMESE_MAP.put('ă', "a"); VIETNAMESE_MAP.put('ắ', "a"); VIETNAMESE_MAP.put('ằ', "a"); VIETNAMESE_MAP.put('ẳ', "a"); VIETNAMESE_MAP.put('ẵ', "a"); VIETNAMESE_MAP.put('ặ', "a");
        VIETNAMESE_MAP.put('â', "a"); VIETNAMESE_MAP.put('ấ', "a"); VIETNAMESE_MAP.put('ầ', "a"); VIETNAMESE_MAP.put('ẩ', "a"); VIETNAMESE_MAP.put('ẫ', "a"); VIETNAMESE_MAP.put('ậ', "a");

        VIETNAMESE_MAP.put('é', "e"); VIETNAMESE_MAP.put('è', "e"); VIETNAMESE_MAP.put('ẻ', "e"); VIETNAMESE_MAP.put('ẽ', "e"); VIETNAMESE_MAP.put('ẹ', "e");
        VIETNAMESE_MAP.put('ê', "e"); VIETNAMESE_MAP.put('ế', "e"); VIETNAMESE_MAP.put('ề', "e"); VIETNAMESE_MAP.put('ể', "e"); VIETNAMESE_MAP.put('ễ', "e"); VIETNAMESE_MAP.put('ệ', "e");

        VIETNAMESE_MAP.put('í', "i"); VIETNAMESE_MAP.put('ì', "i"); VIETNAMESE_MAP.put('ỉ', "i"); VIETNAMESE_MAP.put('ĩ', "i"); VIETNAMESE_MAP.put('ị', "i");

        VIETNAMESE_MAP.put('ó', "o"); VIETNAMESE_MAP.put('ò', "o"); VIETNAMESE_MAP.put('ỏ', "o"); VIETNAMESE_MAP.put('õ', "o"); VIETNAMESE_MAP.put('ọ', "o");
        VIETNAMESE_MAP.put('ô', "o"); VIETNAMESE_MAP.put('ố', "o"); VIETNAMESE_MAP.put('ồ', "o"); VIETNAMESE_MAP.put('ổ', "o"); VIETNAMESE_MAP.put('ỗ', "o"); VIETNAMESE_MAP.put('ộ', "o");
        VIETNAMESE_MAP.put('ơ', "o"); VIETNAMESE_MAP.put('ớ', "o"); VIETNAMESE_MAP.put('ờ', "o"); VIETNAMESE_MAP.put('ở', "o"); VIETNAMESE_MAP.put('ỡ', "o"); VIETNAMESE_MAP.put('ợ', "o");

        VIETNAMESE_MAP.put('ú', "u"); VIETNAMESE_MAP.put('ù', "u"); VIETNAMESE_MAP.put('ủ', "u"); VIETNAMESE_MAP.put('ũ', "u"); VIETNAMESE_MAP.put('ụ', "u");
        VIETNAMESE_MAP.put('ư', "u"); VIETNAMESE_MAP.put('ứ', "u"); VIETNAMESE_MAP.put('ừ', "u"); VIETNAMESE_MAP.put('ử', "u"); VIETNAMESE_MAP.put('ữ', "u"); VIETNAMESE_MAP.put('ự', "u");

        VIETNAMESE_MAP.put('ý', "y"); VIETNAMESE_MAP.put('ỳ', "y"); VIETNAMESE_MAP.put('ỷ', "y"); VIETNAMESE_MAP.put('ỹ', "y"); VIETNAMESE_MAP.put('ỵ', "y");
    }

    public static String toSlug(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }

        String lower = input.toLowerCase();
        StringBuilder sb = new StringBuilder();

        // Thay từng ký tự Việt Nam sang ASCII
        for (char c : lower.toCharArray()) {
            if (VIETNAMESE_MAP.containsKey(c)) {
                sb.append(VIETNAMESE_MAP.get(c));
            } else {
                sb.append(c);
            }
        }

        // Bỏ dấu khác (nếu còn)
        String normalized = Normalizer.normalize(sb.toString(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // Thay ký tự không hợp lệ thành -
        normalized = normalized.replaceAll("[^a-z0-9]+", "-");

        // Bỏ dấu - ở đầu/cuối
        normalized = normalized.replaceAll("^-+|-+$", "");

        return normalized;
    }
}
