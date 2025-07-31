package com.tkt.quizedu.data.dto.request;

import org.springframework.web.multipart.MultipartFile;

import com.tkt.quizedu.data.constant.MatchingType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchingQuestionRequest {
  private String contentA;
  private MultipartFile fileContentA;
  private MatchingType typeA;
  private String contentB;
  private MultipartFile fileContentB;
  private MatchingType typeB;
  private int points;
}
