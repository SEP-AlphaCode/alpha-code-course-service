package site.alphacode.alphacodecourseservice.service.implement;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import site.alphacode.alphacodecourseservice.service.S3Service;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.InputStream;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class S3ServiceImplement implements S3Service {

    private final S3Client s3Client;
    private final Region awsRegion;
    private final StaticCredentialsProvider credentialsProvider;

    @Value("${application.bucket.name}")
    private String bucketName;

    @Override
    public String uploadBytes(byte[] data, String key, String contentType) {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .contentType(contentType)
                        .build(),
                RequestBody.fromBytes(data)
        );

        return buildPublicUrl(key);
    }

    @Override
    public String uploadStream(InputStream inputStream, long contentLength, String key, String contentType) {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .contentType(contentType)
                        .build(),
                RequestBody.fromInputStream(inputStream, contentLength)
        );

        return buildPublicUrl(key);
    }

    @Override
    public String generatePresignedPutUrl(String key, String contentType, long expiresSeconds) {
        try (S3Presigner presigner = S3Presigner.builder()
                .region(awsRegion)
                .credentialsProvider(credentialsProvider)
                .build()) {

            var putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .build();

            var presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(expiresSeconds))
                    .putObjectRequest(putRequest)
                    .build();

            return presigner.presignPutObject(presignRequest).url().toString();
        }
    }

    @Override
    public String buildPublicUrl(String key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName,
                awsRegion.id(),
                key
        );
    }
}
