package com.tkt.quizedu.service.auth;

public interface IAuthenticationService {

  void validateVerificationCode(String userId, String code);
}
