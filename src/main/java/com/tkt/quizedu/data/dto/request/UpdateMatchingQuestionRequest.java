package com.tkt.quizedu.data.dto.request;

import java.util.UUID;

import com.tkt.quizedu.data.constant.MatchingType;

public record UpdateMatchingQuestionRequest(
    UUID id,
    String contentA,
    MatchingType typeA,
    String contentB,
    MatchingType typeB,
    int points) {}
