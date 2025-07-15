package com.tkt.quizedu.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.tkt.quizedu.data.collection.User;
import com.tkt.quizedu.data.dto.request.StudentCreationDTORequest;
import com.tkt.quizedu.data.dto.request.TeacherCreationDTORequest;
import com.tkt.quizedu.data.dto.request.UserCreationDTORequest;
import com.tkt.quizedu.data.dto.response.UserBaseResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {
  User toUser(UserCreationDTORequest req);

  User toUserFromStudent(StudentCreationDTORequest req);

  User toUserFromTeacher(TeacherCreationDTORequest req);

  @Mapping(target = "isActive", source = "active")
  UserBaseResponse toUserBaseResponse(User user);
}
