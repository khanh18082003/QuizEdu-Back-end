package com.tkt.quizedu.service.user;

import com.tkt.quizedu.data.dto.request.ChangePasswordDTORequest;
import com.tkt.quizedu.data.dto.request.UserCreationDTORequest;
import com.tkt.quizedu.data.dto.response.UserBaseResponse;

public interface IUserService {
  UserBaseResponse save(UserCreationDTORequest req);

  void activeUser(String email);

  boolean existsUserByEmail(String email);

  UserBaseResponse getMyProfile();

  void changePassword(ChangePasswordDTORequest request);
}
