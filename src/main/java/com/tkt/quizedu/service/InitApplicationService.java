package com.tkt.quizedu.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tkt.quizedu.data.constant.UserRole;
import com.tkt.quizedu.data.dto.request.UserCreationDTORequest;
import com.tkt.quizedu.service.user.IUserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "INIT-APPLICATION-SERVICE")
public class InitApplicationService {
  IUserService userService;

  PasswordEncoder passwordEncoder;

  @NonFinal
  @Value("${admin.email}")
  String email;

  @NonFinal
  @Value("${admin.password}")
  String password;

  @NonFinal
  @Value("${admin.displayName}")
  String displayName;

  @Transactional
  public void init() {
    log.info("Initializing application...");
    // Add any initialization logic here, such as loading default data or configurations
    if (!userService.existsUserByEmail(email)) {
      log.info("Creating default admin user with email: {}", email);
      // Create and save the default admin user
      UserCreationDTORequest admin =
          new UserCreationDTORequest(
              email, passwordEncoder.encode(password), displayName, UserRole.ADMIN.name());

      // Set other required fields for the admin user
      userService.save(admin);
    } else {
      log.info("Admin user already exists with email: {}", email);
    }
    log.info("Application initialized successfully.");
  }
}
