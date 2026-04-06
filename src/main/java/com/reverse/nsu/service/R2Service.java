package com.reverse.nsu.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class R2Service {

    private final S3Client s3Client;

    @Value("${cloud.r2.bucket-name}")
    private String bucketName;

    @Value("${cloud.r2.public-url}")
    private String publicUrl;

    public R2Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * 이미지 업로드 후 퍼블릭 URL 반환
     * @param file      업로드할 파일
     * @param folder    저장 폴더 (예: "club", "project", "executive")
     * @return          DB에 저장할 퍼블릭 URL
     */
    public String upload(MultipartFile file, String folder) throws IOException {
        String fileName = folder + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

        // DB에 저장할 URL 반환
        return publicUrl + "/" + fileName;
    }

    /**
     * 이미지 삭제
     * @param fileUrl   DB에 저장된 퍼블릭 URL
     */
    public void delete(String fileUrl) {
        String key = fileUrl.replace(publicUrl + "/", "");

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(request);
    }
}