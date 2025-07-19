package com.tkt.quizedu.service.user;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tkt.quizedu.data.collection.CustomUserDetail;
import com.tkt.quizedu.data.collection.User;
import com.tkt.quizedu.data.constant.ErrorCode;
import com.tkt.quizedu.data.constant.UserRole;
import com.tkt.quizedu.data.dto.request.ChangePasswordDTORequest;
import com.tkt.quizedu.data.dto.request.StudentCreationDTORequest;
import com.tkt.quizedu.data.dto.request.TeacherCreationDTORequest;
import com.tkt.quizedu.data.dto.request.UserCreationDTORequest;
import com.tkt.quizedu.data.dto.response.UserBaseResponse;
import com.tkt.quizedu.data.mapper.UserMapper;
import com.tkt.quizedu.data.repository.UserRepository;
import com.tkt.quizedu.exception.QuizException;
import com.tkt.quizedu.utils.SecurityUtils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "USER-SERVICE")
public class UserServiceImpl implements IUserService {
  UserRepository userRepository;
  UserMapper userMapper;
  PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public UserBaseResponse save(UserCreationDTORequest req) {
    validatePasswordMatch(req);

    User user = createUserFromRequest(req);
    setUserActivationStatus(user);

    User savedUser = userRepository.save(user);
    log.info(
        "User created successfully with email: {} and role: {}",
        savedUser.getEmail(),
        savedUser.getRole());

    return userMapper.toUserBaseResponse(savedUser);
  }

  private User createUserFromRequest(UserCreationDTORequest req) {
    encodePassword(req);

    if (req instanceof StudentCreationDTORequest studentReq) {
      return userMapper.toUserFromStudent(studentReq);
    } else if (req instanceof TeacherCreationDTORequest teacherReq) {
      return userMapper.toUserFromTeacher(teacherReq);
    }

    return userMapper.toUser(req);
  }

  private void validatePasswordMatch(UserCreationDTORequest req) {
    if (!req.getPassword().equals(req.getConfirmPassword())) {
      throw new QuizException(ErrorCode.MESSAGE_PASSWORD_NOT_MATCH);
    }
  }

  private void encodePassword(UserCreationDTORequest req) {
    req.setPassword(passwordEncoder.encode(req.getPassword()));
  }

  private void setUserActivationStatus(User user) {
    user.setActive(user.getRole() == UserRole.ADMIN);
  }

  @Override
  public void activeUser(String email) {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new QuizException(ErrorCode.MESSAGE_INVALID_ID));

    user.setActive(true);

    userRepository.save(user);
  }

  @Override
  public boolean existsUserByEmail(String email) {
    return userRepository.existsUserByEmail(email);
  }

  @Override
  public UserBaseResponse getMyProfile() {
    CustomUserDetail userDetail = SecurityUtils.getUserDetail();
    if (userDetail == null) {
      throw new QuizException(ErrorCode.MESSAGE_UNAUTHORIZED);
    }

    User user = userDetail.getUser();

    return userMapper.toProfileResponse(user);
  }

  @Override
  public Optional<User> getUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  @Override
  @Transactional
  public void changePassword(ChangePasswordDTORequest request) {
    User user =
        userRepository
            .findByEmail(request.email())
            .orElseThrow(() -> new QuizException(ErrorCode.MESSAGE_INVALID_ID));
    if (!request.newPassword().equals(request.confirmPassword())) {
      throw new QuizException(ErrorCode.MESSAGE_PASSWORD_NOT_MATCH);
    }

    user.setPassword(passwordEncoder.encode(request.newPassword()));

    userRepository.save(user);
  }
}
