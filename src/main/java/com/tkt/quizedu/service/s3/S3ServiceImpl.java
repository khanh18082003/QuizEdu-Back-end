package com.tkt.quizedu.service.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "S3-SERVICE")
public class S3ServiceImpl implements IS3Service {

  S3Client s3Client;

  @Value("${aws.s3.bucket-name}")
  @NonFinal
  String bucketName;

  @Override
  public String uploadFile(MultipartFile file) {
    String fileName = file.getOriginalFilename();
    PutObjectRequest putObjectRequest =
        PutObjectRequest.builder().bucket(bucketName).key(fileName).build();
    s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileName.getBytes()));
    return fileName;
  }
}
