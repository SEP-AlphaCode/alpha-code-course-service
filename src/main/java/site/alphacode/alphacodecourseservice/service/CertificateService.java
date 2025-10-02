package site.alphacode.alphacodecourseservice.service;

import site.alphacode.alphacodecourseservice.dto.request.create.CreateCertificate;
import site.alphacode.alphacodecourseservice.dto.response.CertificateInformation;

import java.util.UUID;

public interface CertificateService {
    CertificateInformation getByAccountIdAndCourseId(UUID accountId, UUID courseId);
    CertificateInformation create(CreateCertificate createCertificate);
}
