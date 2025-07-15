package com.tkt.quizedu.service.auth;

import java.util.concurrent.TimeUnit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tkt.quizedu.data.collection.CustomUserDetail;
import com.tkt.quizedu.data.collection.User;
import com.tkt.quizedu.data.constant.ErrorCode;
import com.tkt.quizedu.data.constant.TokenType;
import com.tkt.quizedu.data.dto.request.AuthenticationDTORequest;
import com.tkt.quizedu.data.dto.request.ResendCodeDTORequest;
import com.tkt.quizedu.data.dto.response.AuthenticationResponse;
import com.tkt.quizedu.exception.QuizException;
import com.tkt.quizedu.service.jwt.IJwtService;
import com.tkt.quizedu.service.user.CustomUserDetailService;
import com.tkt.quizedu.service.user.IUserService;
import com.tkt.quizedu.utils.CookiesUtils;
import com.tkt.quizedu.utils.GenerateVerificationCode;
import com.tkt.quizedu.utils.SecurityUtils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "AUTHENTICATION-SERVICE")
public class AuthenticationServiceImpl implements IAuthenticationService {
  RedisTemplate<String, Object> redisTemplate;
  IUserService userService;
  AuthenticationManager authenticationManager;
  KafkaTemplate<String, String> kafkaTemplate;
  IJwtService jwtService;
  CustomUserDetailService customUserDetailService;

  @NonFinal
  @Value("${jwt.expirationDay}")
  int expirationDay;

  @NonFinal
  @Value("${jwt.expirationTime}")
  int expirationTime;

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

  @Override
  public AuthenticationResponse authenticate(
      AuthenticationDTORequest req, HttpServletResponse httpServletResponse) {
    Authentication auth;
    try {
      auth =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(req.email(), req.password()));
      SecurityContextHolder.getContext().setAuthentication(auth);
    } catch (AuthenticationException ex) {
      throw new AccessDeniedException(ex.getMessage());
    }

    CustomUserDetail userDetail = (CustomUserDetail) auth.getPrincipal();
    validateUserRole(userDetail, req.role());

    User user = userDetail.getUser();
    handleInactiveUser(user);

    String accessToken =
        jwtService.generateAccessToken(user.getEmail(), userDetail.getAuthorities());
    String refreshToken =
        jwtService.generateRefreshToken(user.getEmail(), userDetail.getAuthorities());

    CookiesUtils.createCookie(
        req.role(), refreshToken, expirationDay * 24 * 60 * 60, "/", httpServletResponse);

    String jit = storeAccessTokenInRedis(accessToken);

    return AuthenticationResponse.builder().accessToken(jit).build();
  }

  @Override
  public AuthenticationResponse refreshToken(HttpServletRequest httpServletRequest, String name) {
    String refreshToken = CookiesUtils.getRefreshTokenFromCookies(httpServletRequest, name);
    String email = jwtService.extractUsername(TokenType.REFRESH_TOKEN, refreshToken);

    // Load the user by username
    CustomUserDetail userDetails =
        (CustomUserDetail) customUserDetailService.loadUserByUsername(email);
    if (userDetails == null) {
      throw new QuizException(ErrorCode.MESSAGE_UNAUTHENTICATED);
    }
    if (!jwtService.validateToken(TokenType.REFRESH_TOKEN, refreshToken, userDetails)) {
      throw new QuizException(ErrorCode.MESSAGE_UNAUTHENTICATED);
    }
    String newAccessToken = jwtService.generateAccessToken(email, userDetails.getAuthorities());
    String jit = storeAccessTokenInRedis(newAccessToken);

    return AuthenticationResponse.builder().accessToken(jit).build();
  }

  @Override
  public void resendVerificationCode(ResendCodeDTORequest req) {
    sendVerificationCodeForInactiveUser(req.id(), req.email(), req.firstName(), req.lastName());
  }

  private String storeAccessTokenInRedis(String accessToken) {
    String key = jwtService.extractId(TokenType.ACCESS_TOKEN, accessToken);
    redisTemplate.opsForValue().set(key, accessToken, expirationTime, TimeUnit.MINUTES);
    return key;
  }

  private void validateUserRole(CustomUserDetail userDetail, String role) {
    if (!userDetail.getAuthorities().containsAll(SecurityUtils.getAuthorities(role))) {
      throw new QuizException(ErrorCode.MESSAGE_UNAUTHENTICATED);
    }
  }

  private void handleInactiveUser(User user) {
    if (user != null && !user.isActive()) {
      sendVerificationCodeForInactiveUser(
          user.getId(), user.getEmail(), user.getFirstName(), user.getLastName());
      throw new QuizException(ErrorCode.MESSAGE_UNAUTHENTICATED);
    }
  }

  private void sendVerificationCodeForInactiveUser(
      String id, String email, String firstName, String lastName) {
    String code = GenerateVerificationCode.generateCode();
    String key = "user:confirmation:" + id;
    redisTemplate.opsForValue().set(key, code, 10, TimeUnit.MINUTES);

    String message =
        String.format("email=%s,name=%s,code=%s", email, firstName + " " + lastName, code);
    kafkaTemplate.send("confirm-account-topic", message);
  }
}
