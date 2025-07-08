package com.tkt.quizedu.service.auth;

import com.tkt.quizedu.data.constant.ErrorCode;
import com.tkt.quizedu.exception.QuizException;
import com.tkt.quizedu.service.user.IUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "AUTHENTICATION-SERVICE")
public class AuthenticationServiceImpl implements IAuthenticationService {
    RedisTemplate<String, Object> redisTemplate;
    IUserService userService;

    @Override
    @Transactional
    public void validateVerificationCode(String userId, String code) {
        String key = "user:confirmation:" + userId;
        String storedCode = (String) redisTemplate.opsForValue().get(key);
        if (storedCode != null && storedCode.equals(code)) {
            // If the code matches, remove it from Redis to prevent reuse
            redisTemplate.delete(key);
            userService.activeUser(userId);
        } else {
            // If the code does not match, throw an exception or handle accordingly
            log.error("Invalid verification code for user: {}", userId);
            throw new QuizException(ErrorCode.MESSAGE_UNAUTHENTICATED);
        }

    }
}
