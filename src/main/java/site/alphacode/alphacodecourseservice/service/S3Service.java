package site.alphacode.alphacodecourseservice.service;

import site.alphacode.alphacodecourseservice.dto.response.PresignResponse;

import java.io.InputStream;

public interface S3Service {
    String uploadBytes(byte[] data, String key, String contentType);

    String uploadStream(InputStream inputStream, long contentLength, String key, String contentType);

    String generatePresignedPutUrl(String key, String contentType, long expiresSeconds);

    String buildPublicUrl(String key);

    PresignResponse generatePresignUrl(String filename, String contentType, String folder, long expiresInSeconds);
}

