package site.alphacode.alphacodecourseservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateCertificate;
import site.alphacode.alphacodecourseservice.dto.response.CertificateInformation;
import site.alphacode.alphacodecourseservice.entity.Certificate;
import site.alphacode.alphacodecourseservice.entity.Course;
import site.alphacode.alphacodecourseservice.exception.ResourceNotFoundException;
import site.alphacode.alphacodecourseservice.grpc.client.UserServiceClient;
import site.alphacode.alphacodecourseservice.repository.CertificateRepository;
import site.alphacode.alphacodecourseservice.repository.CourseRepository;
import site.alphacode.alphacodecourseservice.service.implement.CertificateServiceImplement;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CertificateService Unit Tests")
class CertificateServiceTest {

    @Mock
    private CertificateRepository certificateRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private CertificateServiceImplement certificateService;

    private UUID certificateId;
    private UUID accountId;
    private UUID courseId;
    private Certificate certificate;
    private Course course;
    private CreateCertificate createCertificate;

    @BeforeEach
    void setUp() {
        certificateId = UUID.randomUUID();
        accountId = UUID.randomUUID();
        courseId = UUID.randomUUID();

        course = Course.builder()
                .id(courseId)
                .name("Test Course")
                .status(1)
                .build();

        certificate = Certificate.builder()
                .id(certificateId)
                .accountId(accountId)
                .courseId(courseId)
                .issuedDate(LocalDateTime.now())
                .status(1)
                .build();

        createCertificate = new CreateCertificate();
        createCertificate.setAccountId(accountId);
        createCertificate.setCourseId(courseId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when certificate not found")
    void testGetByAccountIdAndCourseId_NotFound() {
        // Given
        when(certificateRepository.getByAccountIdAndCourseId(accountId, courseId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            certificateService.getByAccountIdAndCourseId(accountId, courseId);
        });
        verify(certificateRepository).getByAccountIdAndCourseId(accountId, courseId);
    }

    @Test
    @DisplayName("Should throw RuntimeException when certificate already exists")
    void testCreate_CertificateExists() {
        // Given
        when(certificateRepository.getByAccountIdAndCourseId(accountId, courseId))
                .thenReturn(Optional.of(certificate));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            certificateService.create(createCertificate);
        });
        verify(certificateRepository).getByAccountIdAndCourseId(accountId, courseId);
        verify(certificateRepository, never()).save(any(Certificate.class));
    }
}

