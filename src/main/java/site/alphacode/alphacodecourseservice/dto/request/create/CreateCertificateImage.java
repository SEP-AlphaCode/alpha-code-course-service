package site.alphacode.alphacodecourseservice.dto.request.create;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCertificateImage {
    private String studentName;
    private String courseName;
    private String date;
    private String certificateId;
}
