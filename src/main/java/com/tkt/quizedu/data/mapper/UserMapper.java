package com.tkt.quizedu.data.mapper;

import com.tkt.quizedu.data.dto.request.*;
import com.tkt.quizedu.data.dto.response.StudentUpdateResponse;
import com.tkt.quizedu.data.dto.response.TeacherUpdateResponse;
import com.tkt.quizedu.data.dto.response.UserUpdateResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.tkt.quizedu.data.collection.User;
import com.tkt.quizedu.data.dto.response.UserBaseResponse;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationDTORequest req);

    User toUserFromStudent(StudentCreationDTORequest req);

    User toUserFromTeacher(TeacherCreationDTORequest req);

    @Mapping(target = "isActive", source = "active")
    UserBaseResponse toUserBaseResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User toUserFromStudentUpdateRequest(StudentUpdateRequest req);

    StudentUpdateResponse toStudentUpdateResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User toUserFromTeacherUpdateRequest(TeacherUpdateRequest req);

    TeacherUpdateResponse toTeacherUpdateResponse(User user);
}
