package site.alphacode.alphacodecourseservice.service.implement;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import site.alphacode.alphacodecourseservice.dto.response.CertificateInformation;
import site.alphacode.alphacodecourseservice.exception.ResourceNotFoundException;
import site.alphacode.alphacodecourseservice.grpc.client.UserServiceClient;
import site.alphacode.alphacodecourseservice.mapper.CertificateMapper;
import site.alphacode.alphacodecourseservice.repository.CertificateRepository;
import site.alphacode.alphacodecourseservice.repository.CourseRepository;
import site.alphacode.alphacodecourseservice.service.CertificateService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CertificateServiceImplement implements CertificateService {
    private final CertificateRepository certificateRepository;
    private final CourseRepository courseRepository;
    private final UserServiceClient userServiceClient;

    @Override
    @Cacheable(value = "certificate_info", key = "{#accountId, #courseId}")
    public CertificateInformation getByAccountIdAndCourseId(UUID accountId, UUID courseId) {
        var certificate = certificateRepository.getByAccountIdAndCourseId(accountId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate với accountId: " + accountId + " và courseId: " + courseId + " không tìm thấy"));
        var course = courseRepository.findActiveCourseById(courseId).orElseThrow(()-> new RuntimeException("Course với id: " + courseId + " không tìm thấy"));
        var account = userServiceClient.getAccount(accountId.toString());
        var userFullName = account.getFullName().isEmpty() ? accountId.toString() : account.getFullName();

        return CertificateMapper.toCertificateInformation(certificate, userFullName, course.getName());
    }
}
