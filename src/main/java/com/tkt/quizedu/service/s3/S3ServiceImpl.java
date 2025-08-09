package com.tkt.quizedu.service.s3;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
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

  @Value("${aws.s3.base-url}")
  @NonFinal
  String baseUrl;

  @Override
  public String uploadFile(MultipartFile file) {
    String fileName = file.getOriginalFilename();
    try {
      PutObjectRequest putObjectRequest =
          PutObjectRequest.builder()
              .bucket(bucketName)
              .key(fileName)
              .contentType(file.getContentType())
              .acl("public-read")
              .build();

      s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
    } catch (IOException e) {
      throw new RuntimeException("Failed to upload file", e);
    }

    //  Encode URL để thay khoảng trắng thành '+'
    String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);

    return baseUrl + encodedFileName;
  }

  @Override
  public File downloadFileToTemp(String s3Url) throws IOException {
    // Extract the key from the S3 URL
    String key = s3Url.substring(s3Url.lastIndexOf("/") + 1);
    File tempFile = File.createTempFile("s3file-", "-" + key);
    // Delete the file so S3 can write to it
    if (tempFile.exists()) {
      tempFile.delete();
    }

    // Download the S3 object directly to the temp file
    s3Client.getObject(
        builder -> builder.bucket(bucketName).key(key).build(),
        ResponseTransformer.toFile(tempFile.toPath()));

    return tempFile;
  }

  @Override
  public void deleteFile(String fileName) {
    try {
      s3Client.deleteObject(request -> request.bucket(bucketName).key(fileName));
    } catch (AwsServiceException awsException) {
      log.error("Failed to delete file from S3: {}", awsException.awsErrorDetails().errorMessage());
      throw new RuntimeException("Failed to delete file from S3", awsException);
    } catch (Exception e) {
      log.error("An unexpected error occurred while deleting file from S3: {}", e.getMessage());
      throw new RuntimeException("An unexpected error occurred while deleting file from S3", e);
    }
  }
}
