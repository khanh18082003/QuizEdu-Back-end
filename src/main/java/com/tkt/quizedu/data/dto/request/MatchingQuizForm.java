package com.tkt.quizedu.data.dto.request;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchingQuizForm {
  private List<MatchingQuestionRequest> questions = new ArrayList<>();
  ;
}
