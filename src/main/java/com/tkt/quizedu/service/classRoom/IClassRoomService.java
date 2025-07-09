package com.tkt.quizedu.service.classRoom;

import com.tkt.quizedu.data.dto.request.ClassRoomRequest;
import com.tkt.quizedu.data.dto.response.ClassRoomResponse;

public interface IClassRoomService {
    ClassRoomResponse createClassRoom(ClassRoomRequest classRoomRequest);
}
