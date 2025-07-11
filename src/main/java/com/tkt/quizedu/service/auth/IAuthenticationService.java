package com.tkt.quizedu.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.tkt.quizedu.data.dto.request.AuthenticationDTORequest;
import com.tkt.quizedu.data.dto.response.AuthenticationResponse;

public interface IAuthenticationService {

  void validateVerificationCode(String userId, String code);

  AuthenticationResponse authenticate(
      AuthenticationDTORequest req, HttpServletResponse httpServletResponse);

  AuthenticationResponse refreshToken(HttpServletRequest httpServletRequest, String role);
}
