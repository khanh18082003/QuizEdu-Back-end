package com.tkt.quizedu.service.user;

import com.tkt.quizedu.data.dto.request.UserCreationDTORequest;
import com.tkt.quizedu.data.dto.response.UserBaseResponse;

public interface IUserService {
  UserBaseResponse save(UserCreationDTORequest req);

  void activeUser(String userId);

  boolean existsUserByEmail(String email);
}
