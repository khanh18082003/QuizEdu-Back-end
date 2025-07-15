package com.tkt.quizedu.data.dto.request;

import com.tkt.quizedu.data.constant.MatchingType;

import java.util.List;

public record MatchingQuestionRequest(
        String contentA,
        MatchingType typeA,
        String contentB,
        MatchingType typeB,
        int points
) {
}
