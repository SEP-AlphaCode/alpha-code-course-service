package site.alphacode.alphacodecourseservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PresignResponse implements Serializable {
    private String key;
    private String uploadUrl;
    private String publicUrl;
    private long expiresInSeconds;
}
