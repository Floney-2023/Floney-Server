package com.floney.floney.common.config;

import org.springframework.beans.factory.annotation.Value;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.net.URI;

@Service
public class AwsService {

    @Autowired
    private S3Client s3Client;

    @Value("${aws.bucketName}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.id}")
    private String accessKeyId;

    @Value("${aws.secret}")
    private String secretAccessKey;

    @Value("${cloud.storage.endpoint:}")
    private String endpoint;

    public String generatePreSignedUrl(String fileName) {
        // Presigner 빌더 생성 (동적 region 사용)
        S3Presigner.Builder presignerBuilder = S3Presigner.builder()
            .region(Region.of(region))  // 하드코딩된 AP_NORTHEAST_2 대신 동적 사용
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKeyId, secretAccessKey)));

        // OCI 엔드포인트 설정 (OCI 사용 시에만 적용)
        if (endpoint != null && !endpoint.isEmpty()) {
            presignerBuilder.endpointOverride(URI.create(endpoint));
        }

        S3Presigner presigner = presignerBuilder.build();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(fileName)
            .build();

        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(builder -> builder
            .putObjectRequest(putObjectRequest)
            .signatureDuration(Duration.ofMinutes(10))
        );

        String url = presignedRequest.url().toString();
        presigner.close();  // 리소스 정리 추가

        return url;
    }
}