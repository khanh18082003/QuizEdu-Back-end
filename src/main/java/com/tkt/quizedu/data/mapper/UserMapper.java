package com.tkt.quizedu.data.mapper;

import org.mapstruct.Mapper;

import com.tkt.quizedu.data.collection.User;
import com.tkt.quizedu.data.dto.request.UserCreationDTORequest;

@Mapper(componentModel = "spring")
public interface UserMapper {
  User toUser(UserCreationDTORequest req);
}
