package com.tkt.quizedu.data.dto.request;

import java.io.Serializable;
import java.util.List;

public record InviteStudentsToClassRoomRequest(List<String> studentEmails, String classRoomId)
    implements Serializable {}
