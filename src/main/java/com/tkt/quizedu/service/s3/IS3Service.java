package com.tkt.quizedu.service.s3;

import org.springframework.web.multipart.MultipartFile;

public interface IS3Service {

  String uploadFile(MultipartFile file);

  void deleteFile(String fileName);
}
