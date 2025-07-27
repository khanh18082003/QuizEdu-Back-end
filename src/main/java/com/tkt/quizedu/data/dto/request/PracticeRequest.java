package com.tkt.quizedu.data.dto.request;

import java.util.List;

public record PracticeRequest(
        List<String> quizIDs,
        int quantityMultipleChoice,
        int quantityMatching
) {
}
