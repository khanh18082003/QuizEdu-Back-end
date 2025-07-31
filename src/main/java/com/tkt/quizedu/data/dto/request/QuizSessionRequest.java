package com.tkt.quizedu.data.dto.request;

import java.time.LocalDateTime;

public record QuizSessionRequest(
    String quizId, String classId, String teacherId) {}
