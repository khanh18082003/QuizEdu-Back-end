package com.tkt.quizedu.data.dto.request;

import java.util.List;
import java.util.UUID;

public record DeleteQuestionMultipleChoiceRequest(List<UUID> questionIds) {}
