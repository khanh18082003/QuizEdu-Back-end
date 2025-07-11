package com.tkt.quizedu.service.jwt;

import java.util.Collection;
import java.util.Date;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.tkt.quizedu.data.constant.TokenType;

public interface IJwtService {
  String generateAccessToken(String username, Collection<? extends GrantedAuthority> authorities);

  String generateRefreshToken(String username, Collection<? extends GrantedAuthority> authorities);

  String extractUsername(TokenType type, String token);

  Date extractExpiration(TokenType type, String token);

  String extractId(TokenType type, String token);

  boolean validateToken(TokenType type, String token, UserDetails userDetails);
}
