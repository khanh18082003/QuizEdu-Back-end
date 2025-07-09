package com.tkt.quizedu.data.mapper;

import com.tkt.quizedu.data.collection.ClassRoom;
import com.tkt.quizedu.data.dto.request.ClassRoomRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClassRoomMapper {
    ClassRoom toClassRoom(ClassRoomRequest req);
}
