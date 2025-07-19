package com.tkt.quizedu.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.tkt.quizedu.data.collection.User;
import com.tkt.quizedu.data.dto.request.StudentCreationDTORequest;
import com.tkt.quizedu.data.dto.request.TeacherCreationDTORequest;
import com.tkt.quizedu.data.dto.request.UserCreationDTORequest;
import com.tkt.quizedu.data.dto.response.StudentProfileResponse;
import com.tkt.quizedu.data.dto.response.TeacherProfileResponse;
import com.tkt.quizedu.data.dto.response.UserBaseResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {
  User toUser(UserCreationDTORequest req);

  User toUserFromStudent(StudentCreationDTORequest req);

  User toUserFromTeacher(TeacherCreationDTORequest req);

  @Mapping(target = "active", source = "active")
  UserBaseResponse toUserBaseResponse(User user);

  @Mapping(target = "active", source = "active")
  StudentProfileResponse toStudentProfileResponse(User user);

  @Mapping(target = "active", source = "active")
  TeacherProfileResponse toTeacherProfileResponse(User user);

  default UserBaseResponse toProfileResponse(User user) {
    return switch (user.getRole()) {
      case STUDENT -> toStudentProfileResponse(user);
      case TEACHER -> toTeacherProfileResponse(user);
      default -> toUserBaseResponse(user);
    };
  }
}
