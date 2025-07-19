package com.tkt.quizedu.service.user;

import java.util.Optional;

import com.tkt.quizedu.data.collection.User;
import com.tkt.quizedu.data.dto.request.ChangePasswordDTORequest;

import com.tkt.quizedu.data.dto.request.StudentUpdateRequest;
import com.tkt.quizedu.data.dto.request.TeacherUpdateRequest;

import com.tkt.quizedu.data.dto.request.UserCreationDTORequest;
import com.tkt.quizedu.data.dto.response.StudentUpdateResponse;
import com.tkt.quizedu.data.dto.response.TeacherUpdateResponse;
import com.tkt.quizedu.data.dto.response.UserBaseResponse;

public interface IUserService {
  UserBaseResponse save(UserCreationDTORequest req);

  void activeUser(String email);

  boolean existsUserByEmail(String email);

  UserBaseResponse getMyProfile();

  Optional<User> getUserByEmail(String email);

  void changePassword(ChangePasswordDTORequest request);


  StudentUpdateResponse updateStudent(StudentUpdateRequest request);

  TeacherUpdateResponse updateTeacher(TeacherUpdateRequest request);
}
