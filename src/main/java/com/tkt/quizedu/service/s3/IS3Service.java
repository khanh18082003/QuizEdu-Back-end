package com.tkt.quizedu.service.s3;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface IS3Service {

  String uploadFile(MultipartFile file);

  File downloadFileToTemp(String s3Url) throws IOException;

  void deleteFile(String fileName);
}
