package site.alphacode.alphacodecourseservice.util;

public class SlugHelper {
    public static String toSlug(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }
        // Chuyển thành lowercase
        String slug = input.toLowerCase();

        // Bỏ dấu tiếng Việt
        slug = java.text.Normalizer.normalize(slug, java.text.Normalizer.Form.NFD);
        slug = slug.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // Thay ký tự không hợp lệ thành dấu -
        slug = slug.replaceAll("[^a-z0-9]+", "-");

        // Bỏ dấu - ở đầu/cuối
        slug = slug.replaceAll("^-+|-+$", "");

        return slug;
    }
}

