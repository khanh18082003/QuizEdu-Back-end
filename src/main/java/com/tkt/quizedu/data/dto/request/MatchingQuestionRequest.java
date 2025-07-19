package com.tkt.quizedu.data.dto.request;

import com.tkt.quizedu.data.constant.MatchingType;

public record MatchingQuestionRequest(
    String contentA, MatchingType typeA, String contentB, MatchingType typeB, int points) {}
