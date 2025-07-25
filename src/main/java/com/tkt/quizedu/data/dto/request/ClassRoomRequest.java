package com.tkt.quizedu.data.dto.request;

import java.util.List;

public record ClassRoomRequest(
    String name, String description, List<String> assignedQuizIds, boolean isActive)
    implements java.io.Serializable {}
