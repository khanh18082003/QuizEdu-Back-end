package com.tkt.quizedu.service.user;

import com.tkt.quizedu.data.collection.User;
import com.tkt.quizedu.data.constant.ErrorCode;
import com.tkt.quizedu.data.dto.response.UserBaseResponse;
import com.tkt.quizedu.exception.QuizException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tkt.quizedu.data.constant.UserRole;
import com.tkt.quizedu.data.dto.request.UserCreationDTORequest;
import com.tkt.quizedu.data.mapper.UserMapper;
import com.tkt.quizedu.data.repository.UserRepository;

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

        var user = userMapper.toUser(req.withPassword(passwordEncoder.encode(req.password())));

        if (user.getRole() == UserRole.ADMIN) {
            user.setActive(true);
        }

        return userMapper.toUserBaseResponse(userRepository.save(user));
    }

    @Override
    public void activeUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new QuizException(ErrorCode.MESSAGE_INVALID_ID)
        );

        user.setActive(true);

        userRepository.save(user);
    }

    @Override
    public boolean existsUserByEmail(String email) {
        return userRepository.existsUserByEmail(email);
    }
}
