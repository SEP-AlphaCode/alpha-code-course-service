package site.alphacode.alphacodecourseservice.mapper;

import site.alphacode.alphacodecourseservice.dto.response.CertificateInformation;
import site.alphacode.alphacodecourseservice.entity.Certificate;

public class CertificateMapper {
    public static CertificateInformation toCertificateInformation(Certificate certificate, String studentName, String courseName) {
        if (certificate == null) {
            return null;
        }
        CertificateInformation dto = new CertificateInformation();
        dto.setId(certificate.getId());
        dto.setAccountId(certificate.getAccountId());
        dto.setCourseId(certificate.getCourseId());
        dto.setIssuedDate(certificate.getIssuedDate());
        dto.setAccountName(studentName);
        dto.setCourseName(courseName);
        dto.setStatus(certificate.getStatus());
        return dto;
    }
}
