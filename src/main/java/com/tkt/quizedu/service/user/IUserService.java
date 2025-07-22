package com.tkt.quizedu.service.user;

import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.tkt.quizedu.data.collection.User;
import com.tkt.quizedu.data.dto.request.*;
import com.tkt.quizedu.data.dto.request.ChangePasswordDTORequest;
import com.tkt.quizedu.data.dto.request.UserCreationDTORequest;
import com.tkt.quizedu.data.dto.response.*;
import com.tkt.quizedu.data.dto.response.UserBaseResponse;

public interface IUserService {
  UserBaseResponse save(UserCreationDTORequest req);

  void activeUser(String email);

  boolean existsUserByEmail(String email);

  UserBaseResponse getMyProfile();

  Optional<User> getUserByEmail(String email);

  void changePassword(ChangePasswordDTORequest request);

  UserBaseResponse updateProfile(UserUpdateDTORequest req, MultipartFile avatar);

  PaginationResponse<ClassroomBaseResponse> getAllClassRooms(int page, int pageSize);
}
