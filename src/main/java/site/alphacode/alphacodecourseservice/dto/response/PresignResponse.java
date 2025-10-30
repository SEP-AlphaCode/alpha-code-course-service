package site.alphacode.alphacodecourseservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PresignResponse {
    private String key;
    private String uploadUrl;
    private String publicUrl;
    private long expiresInSeconds;
}
