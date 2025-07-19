package com.tkt.quizedu.service.s3;


import com.tkt.quizedu.data.collection.CustomUserDetail;
import com.tkt.quizedu.data.collection.User;
import com.tkt.quizedu.data.repository.UserRepository;
import com.tkt.quizedu.utils.SecurityUtils;

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

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "S3-SERVICE")
public class S3ServiceImpl implements IS3Service {

  S3Client s3Client;

  private final UserRepository userRepository;


  @Value("${aws.s3.bucket-name}")
  @NonFinal
  String bucketName;

  @Override
  public String uploadFile(MultipartFile file) {
    String fileName = file.getOriginalFilename();
    CustomUserDetail userDetail = SecurityUtils.getUserDetail();

    try {
      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
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

    User user = userRepository.findById(userDetail.getUser().getId())
            .orElseThrow(() -> new RuntimeException("User Not Found"));

    String avatarUrl = "https://quiz-edu-service.s3.ap-northeast-1.amazonaws.com/" + encodedFileName;
    user.setAvatar(avatarUrl);
    userRepository.save(user);

    return avatarUrl;
  }
}
