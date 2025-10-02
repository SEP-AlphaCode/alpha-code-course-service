package site.alphacode.alphacodecourseservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateCertificate;
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

    @PostMapping()
    @Operation(summary = "Create Certificate", description = "Create a new certificate for a user who has completed a course. Admin test only.")
    @PreAuthorize("hasAuthority('ROLE_Admin')")
    public CertificateInformation createCertificate(@RequestBody CreateCertificate createCertificate){
        return certificateService.create(createCertificate);
    }
}
