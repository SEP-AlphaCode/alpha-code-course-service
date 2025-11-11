package site.alphacode.alphacodecourseservice.service.implement;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateCertificate;
import site.alphacode.alphacodecourseservice.dto.response.CertificateInformation;
import site.alphacode.alphacodecourseservice.entity.Certificate;
import site.alphacode.alphacodecourseservice.exception.ResourceNotFoundException;
import site.alphacode.alphacodecourseservice.grpc.client.UserServiceClient;
import site.alphacode.alphacodecourseservice.mapper.CertificateMapper;
import site.alphacode.alphacodecourseservice.repository.CertificateRepository;
import site.alphacode.alphacodecourseservice.repository.CourseRepository;
import site.alphacode.alphacodecourseservice.service.CertificateService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CertificateServiceImplement implements CertificateService {
    private final CertificateRepository certificateRepository;
    private final CourseRepository courseRepository;
    private final UserServiceClient userServiceClient;

    @Override
    @Cacheable(value = "certificate_info", key = "{#accountId, #courseId}")
    public CertificateInformation getByAccountIdAndCourseId(UUID accountId, UUID courseId) {
        var certificate = certificateRepository.getByAccountIdAndCourseId(accountId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate với accountId: " + accountId + " và courseId: " + courseId + " không tìm thấy"));
        var course = courseRepository.findNoneDeleteCourseById(courseId).orElseThrow(()-> new RuntimeException("Course với id: " + courseId + " không tìm thấy"));
        var account = userServiceClient.getAccount(accountId.toString());
        var userFullName = account.getFullName().isEmpty() ? accountId.toString() : account.getFullName();

        return CertificateMapper.toCertificateInformation(certificate, userFullName, course.getName());
    }

    @Override
    @Transactional
    @Cacheable(value = "certificate_info", key = "{#createCertificate.accountId, #createCertificate.courseId}")
    public CertificateInformation create(CreateCertificate createCertificate) {
        var certificate = certificateRepository.getByAccountIdAndCourseId(
                createCertificate.getAccountId(), createCertificate.getCourseId());

        if (certificate.isPresent()) {
            log.warn("Certificate already exists for accountId={} courseId={}",
                    createCertificate.getAccountId(), createCertificate.getCourseId());
            throw new RuntimeException("Certificate đã tồn tại");
        }

        var course = courseRepository.findActiveCourseById(createCertificate.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course với id: " + createCertificate.getCourseId() + " không tìm thấy"));

        // fallback tên người dùng
        String userFullName = createCertificate.getAccountId().toString();

        try {
            var account = userServiceClient.getAccount(createCertificate.getAccountId().toString());
            if (account != null && account.getFullName() != null && !account.getFullName().isEmpty()) {
                userFullName = account.getFullName();
            }
        } catch (Exception e) {
            log.warn("Không lấy được account từ UserService, dùng fallback accountId={}, error={}",
                    createCertificate.getAccountId(), e.getMessage());
        }

        Certificate newCertificate = new Certificate();
        newCertificate.setAccountId(createCertificate.getAccountId());
        newCertificate.setCourseId(createCertificate.getCourseId());
        newCertificate.setIssuedDate(LocalDateTime.now());
        newCertificate.setStatus(1);

        newCertificate = certificateRepository.save(newCertificate);
        log.info("Created certificate id={} for accountId={} and courseId={}",
                newCertificate.getId(), createCertificate.getAccountId(), createCertificate.getCourseId());

        return CertificateMapper.toCertificateInformation(newCertificate, userFullName, course.getName());
    }

}
