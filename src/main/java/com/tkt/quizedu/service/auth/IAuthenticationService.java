package com.tkt.quizedu.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.tkt.quizedu.data.dto.request.AuthenticationDTORequest;
import com.tkt.quizedu.data.dto.request.CreatePasswordDTORequest;
import com.tkt.quizedu.data.dto.request.ForgotPasswordDTORequest;
import com.tkt.quizedu.data.dto.request.ResendCodeDTORequest;
import com.tkt.quizedu.data.dto.response.AuthenticationResponse;

public interface IAuthenticationService {

  void validateVerificationCode(String email, String code);

  AuthenticationResponse authenticate(
      AuthenticationDTORequest req, HttpServletResponse httpServletResponse);

  AuthenticationResponse refreshToken(HttpServletRequest httpServletRequest);

  void logout(HttpServletRequest request, HttpServletResponse response);

  void resendVerificationCode(ResendCodeDTORequest req);

  void verifyEmail(ForgotPasswordDTORequest req);

  AuthenticationResponse outboundAuthenticate(String code, String role, HttpServletResponse res);

  void createPassword(CreatePasswordDTORequest req);
}
