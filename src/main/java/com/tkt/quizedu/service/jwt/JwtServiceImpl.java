package com.tkt.quizedu.service.jwt;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.tkt.quizedu.data.constant.ErrorCode;
import com.tkt.quizedu.data.constant.TokenType;
import com.tkt.quizedu.exception.QuizException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "JWT-SERVICE")
public class JwtServiceImpl implements IJwtService {
  @Value("${jwt.accessToken}")
  @NonFinal
  String accessToken;

  @Value("${jwt.refreshToken}")
  @NonFinal
  String refreshToken;

  @Value("${jwt.expirationTime}")
  @NonFinal
  long expirationTime;

  @Value("${jwt.expirationDay}")
  @NonFinal
  long expirationDay;

  @Value("${jwt.issuer}")
  @NonFinal
  String issuer;

  RedisTemplate<String, Object> redisTemplate;

  @Override
  public String generateAccessToken(
      String username, Collection<? extends GrantedAuthority> authorities) {
    log.info("Generate access token for user {} with authorities {}", username, authorities);
    Map<String, Object> headers = new HashMap<>();
    headers.put("typ", "JWT");
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", authorities);

    long expirationTime = this.expirationTime;

    return Jwts.builder()
        .setHeader(headers)
        .setClaims(claims)
        .setSubject(username)
        .setIssuer(issuer)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * expirationTime))
        .setId(UUID.randomUUID().toString())
        .signWith(getKey(TokenType.ACCESS_TOKEN))
        .compact();
  }

  @Override
  public String generateRefreshToken(
      String username, Collection<? extends GrantedAuthority> authorities) {
    log.info("Generate refresh token for user {} with authorities {}", username, authorities);
    Map<String, Object> headers = new HashMap<>();
    headers.put("typ", "JWT");
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", authorities);

    return Jwts.builder()
        .setHeader(headers)
        .setClaims(claims)
        .setSubject(username)
        .setIssuer(issuer)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * expirationDay))
        .setId(UUID.randomUUID().toString())
        .signWith(getKey(TokenType.REFRESH_TOKEN))
        .compact();
  }

  @Override
  public String extractUsername(TokenType type, String token) {
    return extractClaims(type, token, Claims::getSubject);
  }

  @Override
  public Date extractExpiration(TokenType type, String token) {
    return extractClaims(type, token, Claims::getExpiration);
  }

  @Override
  public String extractId(TokenType type, String token) {
    return extractClaims(type, token, Claims::getId);
  }

  @Override
  public boolean validateToken(TokenType type, String token, UserDetails userDetails) {
    String username = extractUsername(type, token);

    return username.equals(userDetails.getUsername())
        && !isTokenExpired(type, token)
        && isWhitelisted(extractId(type, token));
  }

  private boolean isWhitelisted(String jit) {
    return redisTemplate.hasKey(jit);
  }

  private boolean isTokenExpired(TokenType type, String token) {
    return extractExpiration(type, token).before(new Date());
  }

  private Claims extractAllClaim(TokenType type, String token) {
    try {
      return Jwts.parserBuilder()
          .setSigningKey(getKey(type))
          .build()
          .parseClaimsJws(token)
          .getBody();
    } catch (ExpiredJwtException ex) {
      return ex.getClaims();
    } catch (SignatureException ex) {
      throw new QuizException(ErrorCode.MESSAGE_UNAUTHENTICATED);
    }
  }

  private <T> T extractClaims(TokenType type, String token, Function<Claims, T> claimsExtractor) {
    return claimsExtractor.apply(extractAllClaim(type, token));
  }

  private Key getKey(TokenType type) {
    switch (type) {
      case ACCESS_TOKEN -> {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessToken));
      }
      case REFRESH_TOKEN -> {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshToken));
      }
      default -> throw new IllegalArgumentException("Invalid token type");
    }
  }
}
