package com.tkt.quizedu.data.dto.request;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateMatchingQuestionRequest {
  private Integer timeLimit;
  private List<UpdateMatchingQuestion> questions = new ArrayList<>();
}
