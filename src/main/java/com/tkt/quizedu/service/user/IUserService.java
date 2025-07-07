package com.tkt.quizedu.service.user;

import com.tkt.quizedu.data.dto.request.UserCreationDTORequest;

public interface IUserService {
  void save(UserCreationDTORequest req);

  boolean existsUserByEmail(String email);
}
