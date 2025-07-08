package com.tkt.quizedu.data.mapper;

import com.tkt.quizedu.data.dto.response.UserBaseResponse;
import org.mapstruct.Mapper;

import com.tkt.quizedu.data.collection.User;
import com.tkt.quizedu.data.dto.request.UserCreationDTORequest;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationDTORequest req);

    @Mapping(target = "isActive", source = "active")
    UserBaseResponse toUserBaseResponse(User user);
}
