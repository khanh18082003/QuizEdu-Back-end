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
import com.tkt.quizedu.data.constant.UserRole;
import com.tkt.quizedu.data.dto.request.AuthenticationDTORequest;
import com.tkt.quizedu.data.dto.request.ExchangeTokenRequest;
import com.tkt.quizedu.data.dto.request.ForgotPasswordDTORequest;
import com.tkt.quizedu.data.dto.request.ResendCodeDTORequest;
import com.tkt.quizedu.data.dto.response.AuthenticationResponse;
import com.tkt.quizedu.data.repository.UserRepository;
import com.tkt.quizedu.data.repository.httpClient.OutboundIdentityClient;
import com.tkt.quizedu.data.repository.httpClient.OutboundUserClient;
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
  UserRepository userRepository;
  AuthenticationManager authenticationManager;
  KafkaTemplate<String, String> kafkaTemplate;
  IJwtService jwtService;
  CustomUserDetailService customUserDetailService;
  OutboundIdentityClient outboundIdentityClient;
  OutboundUserClient outboundUserClient;

  @NonFinal
  @Value("${jwt.expirationDay}")
  int expirationDay;

  @NonFinal
  @Value("${jwt.expirationTime}")
  int expirationTime;

  @NonFinal
  @Value("${outbound.google.identity.client-id}")
  String clientId;

  @NonFinal
  @Value("${outbound.google.identity.client-secret}")
  String clientSecret;

  @NonFinal
  @Value("${outbound.google.identity.redirect-uri}")
  String redirectUri;

  @NonFinal
  @Value("${outbound.google.identity.grant-type}")
  String grantType;

  private static final String REFRESH_TOKEN_NAME = "SECURE_ID";

  @Override
  @Transactional
  public void validateVerificationCode(String email, String code) {
    String key = "user:confirmation:" + email;
    String storedCode = (String) redisTemplate.opsForValue().get(key);
    if (storedCode != null && storedCode.equals(code)) {
      // If the code matches, remove it from Redis to prevent reuse
      redisTemplate.delete(key);
      // Activate the user
      userService.activeUser(email);
    } else {
      // If the code does not match, throw an exception or handle accordingly
      log.error("Invalid verification code for user: {}", email);
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
        REFRESH_TOKEN_NAME, refreshToken, expirationDay * 24 * 60 * 60, "/", httpServletResponse);

    storeAccessTokenInRedis(accessToken, user.getEmail());

    return AuthenticationResponse.builder()
        .accessToken(accessToken)
        .role(user.getRole().name())
        .build();
  }

  @Override
  public AuthenticationResponse refreshToken(HttpServletRequest httpServletRequest) {
    String refreshToken =
        CookiesUtils.getRefreshTokenFromCookies(httpServletRequest, REFRESH_TOKEN_NAME);
    String email = jwtService.extractUsername(TokenType.REFRESH_TOKEN, refreshToken);

    // Load the user by username
    CustomUserDetail userDetail =
        (CustomUserDetail) customUserDetailService.loadUserByUsername(email);
    if (!jwtService.validateToken(TokenType.REFRESH_TOKEN, refreshToken, userDetail)) {
      throw new QuizException(ErrorCode.MESSAGE_UNAUTHENTICATED);
    }

    String newAccessToken = jwtService.generateAccessToken(email, userDetail.getAuthorities());
    storeAccessTokenInRedis(newAccessToken, email);

    return AuthenticationResponse.builder()
        .accessToken(newAccessToken)
        .role(userDetail.getUser().getRole().name())
        .build();
  }

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response) {
    String header = request.getHeader("Authorization");
    String email = "";
    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring(7); // Extract the token
      email = jwtService.extractUsername(TokenType.ACCESS_TOKEN, token);
      removeAccessTokenFromRedis(email);
    } else {
      log.warn("No Bearer token found in request header for logout.");
    }

    CookiesUtils.clearCookie(email, "/", response);

    SecurityContextHolder.clearContext();
    log.info("User logged out successfully.");
  }

  @Override
  public void resendVerificationCode(ResendCodeDTORequest req) {
    sendVerificationCode(req.email(), req.firstName(), req.lastName());
  }

  @Override
  public void verifyEmail(ForgotPasswordDTORequest req) {
    sendVerificationCode(req.email(), "", "");
  }

  @Override
  public AuthenticationResponse outboundAuthenticate(
      String code, String role, HttpServletResponse res) {
    var exchangeTokenResponse =
        outboundIdentityClient.exchangeToken(
            ExchangeTokenRequest.builder()
                .code(code)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(grantType)
                .redirectUri(redirectUri)
                .build());

    var userInfo = outboundUserClient.getUserInfo("json", exchangeTokenResponse.getAccessToken());

    User user =
        userService
            .getUserByEmail(userInfo.getEmail())
            .orElseGet(
                () -> {
                  User newUser =
                      User.builder()
                          .email(userInfo.getEmail())
                          .firstName(userInfo.getGivenName())
                          .lastName(userInfo.getFamilyName())
                          .avatar(userInfo.getPicture())
                          .role(UserRole.valueOf(role))
                          .isActive(true)
                          .build();
                  return userRepository.save(newUser);
                });
    if (user.getAvatar() == null || user.getAvatar().isEmpty()) {
      user.setAvatar(userInfo.getPicture());
      userRepository.save(user);
    }

    if (!user.getRole().name().equals(role)) {
      throw new QuizException(ErrorCode.MESSAGE_UNAUTHENTICATED);
    }

    if (!user.isActive()) {
      user.setActive(true);
    }

    var authorities = SecurityUtils.getAuthorities(role);
    String accessToken = jwtService.generateAccessToken(user.getEmail(), authorities);
    String refreshToken = jwtService.generateRefreshToken(user.getEmail(), authorities);

    CookiesUtils.createCookie(
        REFRESH_TOKEN_NAME, refreshToken, expirationDay * 24 * 60 * 60, "/", res);

    storeAccessTokenInRedis(accessToken, user.getEmail());

    return AuthenticationResponse.builder().accessToken(accessToken).role(role).build();
  }

  private void storeAccessTokenInRedis(String accessToken, String email) {
    redisTemplate.opsForValue().set(email, accessToken, expirationTime, TimeUnit.MINUTES);
  }

  private void removeAccessTokenFromRedis(String email) {
    redisTemplate.delete(email);
  }

  private void validateUserRole(CustomUserDetail userDetail, String role) {
    if (!userDetail.getAuthorities().containsAll(SecurityUtils.getAuthorities(role))) {
      throw new QuizException(ErrorCode.MESSAGE_UNAUTHENTICATED);
    }
  }

  private void handleInactiveUser(User user) {
    if (user != null && !user.isActive()) {
      sendVerificationCode(user.getEmail(), user.getFirstName(), user.getLastName());
      throw new QuizException(ErrorCode.MESSAGE_UNAUTHENTICATED);
    }
  }

  private void sendVerificationCode(String email, String firstName, String lastName) {
    String code = GenerateVerificationCode.generateCode();
    String key = "user:confirmation:" + email;
    redisTemplate.opsForValue().set(key, code, 10, TimeUnit.MINUTES);

    String message =
        String.format("email=%s,name=%s,code=%s", email, firstName + " " + lastName, code);
    kafkaTemplate.send("confirm-account-topic", message);
  }
}
