package com.tkt.quizedu.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

  @Value("${aws.s3.access-key}")
  private String accessKey;

  @Value("${aws.s3.secret-key}")
  private String secretKey;

  @Value("${aws.s3.bucket-name}")
  private String bucketName;

  @Bean
  public S3Client s3Client() {
    return S3Client.builder()
        .credentialsProvider(() -> AwsBasicCredentials.create(accessKey, secretKey))
        .region(Region.AP_NORTHEAST_1)
        .build();
  }
}
