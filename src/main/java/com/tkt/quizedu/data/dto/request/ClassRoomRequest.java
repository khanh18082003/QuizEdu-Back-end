package com.tkt.quizedu.data.dto.request;

public record ClassRoomRequest(String name, String description, boolean isActive)
    implements java.io.Serializable {}
