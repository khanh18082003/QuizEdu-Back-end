package com.tkt.quizedu.data.dto.request;

import com.tkt.quizedu.data.constant.MatchingType;

import java.util.UUID;

public record UpdateMatchingQuestionRequest(
        UUID id,
        String contentA,
        MatchingType typeA,
        String contentB,
        MatchingType typeB,
        int points
) {
}
