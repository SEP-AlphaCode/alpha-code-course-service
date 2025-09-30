package site.alphacode.alphacodecourseservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CertificateInformation implements Serializable {
    private UUID id;
    private UUID accountId;
    private UUID courseId;
    private String accountName;
    private String courseName;
    private LocalDateTime issuedDate;
    private Integer status;
}
