package com.tkt.quizedu.service.s3;

import java.io.File;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface IS3Service {

  String uploadFile(MultipartFile file);

  File downloadFileToTemp(String s3Url) throws IOException;

  void deleteFile(String fileName);
}
