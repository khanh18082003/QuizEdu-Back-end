package com.tkt.quizedu.data.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MatchingQuizForm {
    private List<MatchingQuestionRequest> questions = new ArrayList<>();;
}
