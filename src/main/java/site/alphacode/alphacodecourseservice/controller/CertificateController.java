package site.alphacode.alphacodecourseservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.alphacode.alphacodecourseservice.dto.response.CertificateInformation;
import site.alphacode.alphacodecourseservice.service.CertificateService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/certificates")
@RequiredArgsConstructor
@Tag(name = "Certificates", description = "Certificate management APIs")
public class CertificateController
{
    private final CertificateService certificateService;

    @GetMapping("/get-by-account-course/{accountId}/{courseId}")
    public CertificateInformation getByAccountIdAndCourseId(@PathVariable UUID accountId,@PathVariable UUID courseId) {
        return certificateService.getByAccountIdAndCourseId(accountId, courseId);
    }
}
