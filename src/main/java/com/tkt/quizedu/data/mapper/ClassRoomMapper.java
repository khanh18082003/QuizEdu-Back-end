package com.tkt.quizedu.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.tkt.quizedu.data.collection.ClassRoom;
import com.tkt.quizedu.data.dto.request.ClassRoomRequest;
import com.tkt.quizedu.data.dto.response.ClassRoomResponse;

@Mapper(componentModel = "spring")
public interface ClassRoomMapper {
  ClassRoom toClassRoom(ClassRoomRequest req);

  @Mapping(target = "isActive", source = "active")
  ClassRoomResponse toClassRoomResponse(ClassRoom classRoom);


}
