package com.floney.floney.common.config;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import java.net.URI;

@Configuration
public class AwsConfig {

    @Value("${aws.id}")
    private String accessKeyId;

    @Value("${aws.secret}")
    private String secretAccessKey;

    @Value("${aws.region}")
    private String region;

    @Value("${cloud.storage.endpoint:}")
    private String endpoint;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        S3ClientBuilder builder = S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(awsCreds));

        // OCI 엔드포인트 설정 (OCI 사용 시에만 적용)
        if (endpoint != null && !endpoint.isEmpty()) {
            builder.endpointOverride(URI.create(endpoint));
            builder.forcePathStyle(true);  // OCI S3 호환 모드 필수
        }

        return builder.build();
    }
}