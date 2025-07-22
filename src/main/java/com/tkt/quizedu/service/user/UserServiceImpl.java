package com.tkt.quizedu.service.user;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tkt.quizedu.data.collection.ClassRoom;
import com.tkt.quizedu.data.collection.CustomUserDetail;
import com.tkt.quizedu.data.collection.User;
import com.tkt.quizedu.data.constant.ErrorCode;
import com.tkt.quizedu.data.constant.UserRole;
import com.tkt.quizedu.data.dto.request.*;
import com.tkt.quizedu.data.dto.response.ClassroomBaseResponse;
import com.tkt.quizedu.data.dto.response.PaginationResponse;
import com.tkt.quizedu.data.dto.response.UserBaseResponse;
import com.tkt.quizedu.data.mapper.UserMapper;
import com.tkt.quizedu.data.repository.ClassRoomRepository;
import com.tkt.quizedu.data.repository.UserRepository;
import com.tkt.quizedu.exception.QuizException;
import com.tkt.quizedu.service.classroom.IClassRoomService;
import com.tkt.quizedu.service.s3.IS3Service;
import com.tkt.quizedu.utils.SecurityUtils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "USER-SERVICE")
public class UserServiceImpl implements IUserService {
  UserRepository userRepository;
  UserMapper userMapper;
  PasswordEncoder passwordEncoder;
  IS3Service s3Service;
  IClassRoomService classRoomService;
  ClassRoomRepository classRoomRepository;

  @Value(("${aws.s3.base-url}"))
  @NonFinal
  String baseUrl;

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
    if (req.getRole().equals(UserRole.ADMIN.name())) {
      return;
    }
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

  @Override
  @Transactional
  public UserBaseResponse updateProfile(UserUpdateDTORequest req, MultipartFile avatar) {
    CustomUserDetail userDetail = SecurityUtils.getUserDetail();
    if (userDetail == null) {
      throw new QuizException(ErrorCode.MESSAGE_UNAUTHORIZED);
    }
    User user = userDetail.getUser();

    if (req instanceof StudentUpdateRequest studentUpdateRequest) {
      userMapper.mergeUserFromStudentUpdateRequest(user, studentUpdateRequest);
    } else if (req instanceof TeacherUpdateRequest teacherUpdateRequest) {
      userMapper.mergeUserFromTeacherUpdateRequest(user, teacherUpdateRequest);
    } else {
      userMapper.mergeUserFromUserUpdateRequest(user, req);
    }

    if (avatar != null && !avatar.isEmpty()) {
      String avatarUrl = s3Service.uploadFile(avatar);
      if (user.getAvatar() != null) {
        String fileName = user.getAvatar().replace(baseUrl, "");
        s3Service.deleteFile(fileName);
      }
      user.setAvatar(avatarUrl);
    }

    return userMapper.toProfileResponse(userRepository.save(user));
  }

  @Override
  public PaginationResponse<ClassroomBaseResponse> getAllClassRooms(int page, int pageSize) {
    CustomUserDetail userDetail = SecurityUtils.getUserDetail();
    if (userDetail == null) {
      throw new QuizException(ErrorCode.MESSAGE_UNAUTHORIZED);
    }
    User user = userDetail.getUser();
    Pageable pageable = PageRequest.of(page - 1, pageSize);

    List<String> classIds = user.getClassIds();
    if (user.getRole().equals(UserRole.TEACHER)) {
      classIds =
          classRoomRepository.findByTeacherId(user.getId()).stream().map(ClassRoom::getId).toList();
    }

    Page<ClassroomBaseResponse> classRooms = classRoomService.getClassroomByIds(classIds, pageable);

    return PaginationResponse.<ClassroomBaseResponse>builder()
        .data(classRooms.getContent())
        .page(page)
        .pageSize(pageSize)
        .pages(classRooms.getTotalPages())
        .total(classRooms.getTotalElements())
        .build();
  }
}
