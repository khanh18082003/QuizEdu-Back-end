package com.tkt.quizedu.component;

import java.io.IOException;
import java.time.LocalDateTime;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tkt.quizedu.data.constant.ErrorCode;
import com.tkt.quizedu.data.constant.TokenType;
import com.tkt.quizedu.data.dto.response.ErrorApiResponse;
import com.tkt.quizedu.service.jwt.IJwtService;
import com.tkt.quizedu.service.user.CustomUserDetailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "CUSTOM-PRE-FILTER-REQUEST")
public class CustomPreFilterRequest extends OncePerRequestFilter {

  private final IJwtService jwtService;
  private final CustomUserDetailService userInfoService;
  private final RedisTemplate<String, Object> redisTemplate;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws IOException {

    log.info("Filter request: {}", request.getRequestURI());

    try {
      String authHeader = request.getHeader("Authorization");
      String username = null;
      String token = null;

      // Check if the header starts with "Bearer"
      if (authHeader != null && authHeader.startsWith("Bearer ")) {
        token = authHeader.substring(7); // extract token
        username = extractUsernameFromToken(token);
      }

      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        authenticateUser(request, token, username);
      }

      filterChain.doFilter(request, response);

    } catch (Exception ex) {
      log.error("Authentication error in filter: {}", ex.getMessage(), ex);
      writeErrorResponse(response, "Authentication failed: " + ex.getMessage());
    }
  }

  private String extractUsernameFromToken(String token) {
    try {
      return jwtService.extractUsername(TokenType.ACCESS_TOKEN, token);

    } catch (Exception ex) {
      log.error("Error extracting username from token: {}", ex.getMessage());
      throw new AccessDeniedException("Invalid or expired token");
    }
  }

  private void authenticateUser(HttpServletRequest request, String token, String username) {
    try {
      UserDetails userDetails = userInfoService.loadUserByUsername(username);

      if (jwtService.validateToken(TokenType.ACCESS_TOKEN, token, userDetails)) {
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authToken);
        log.info("User {} authenticated successfully", username);
      } else {
        throw new AccessDeniedException("Token validation failed");
      }
    } catch (Exception ex) {
      log.error("User authentication failed for {}: {}", username, ex.getMessage());
      throw new AccessDeniedException("User authentication failed");
    }
  }

  private void writeErrorResponse(HttpServletResponse response, String message) throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(errorResponse(message));
  }

  private String errorResponse(String message) {
    try {
      ErrorApiResponse errorApiResponse =
          ErrorApiResponse.builder()
              .timeStamp(LocalDateTime.now())
              .code(ErrorCode.MESSAGE_UNAUTHENTICATED.getCode())
              .status(HttpStatus.UNAUTHORIZED.value())
              .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
              .message(message)
              .build();

      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
      return objectMapper.writeValueAsString(errorApiResponse);
    } catch (Exception e) {
      log.error("Error creating JSON response", e);
      return "{\"error\":\"Internal server error\",\"status\":401}";
    }
  }
}
