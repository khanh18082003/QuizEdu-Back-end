package com.tkt.quizedu.data.dto.request;

import java.util.List;

public record ClassRoomRequest(
    String name, String description, boolean isActive)
    implements java.io.Serializable {}
