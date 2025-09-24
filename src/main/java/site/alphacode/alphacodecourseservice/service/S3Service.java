package site.alphacode.alphacodecourseservice.service;

import java.io.File;

public interface S3Service {
    String uploadBytes(byte[] data, String key, String contentType);
}

