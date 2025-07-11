package com.tkt.quizedu.component;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tkt.quizedu.data.constant.TokenType;
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

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    log.info("Filter request: {}", request.getRequestURI());
    String authHeader = request.getHeader("Authorization");
    String token = null;
    String username = null;

    // Check if the header starts with "Bearer"
    if (authHeader != null && authHeader.startsWith("Bearer")) {
      token = authHeader.substring(7); // extract token
      username = jwtService.extractUsername(TokenType.ACCESS_TOKEN, token); // extract username
    }

    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = userInfoService.loadUserByUsername(username);
      if (jwtService.validateToken(TokenType.ACCESS_TOKEN, token, userDetails)) {
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        // Set the authentication in the security context
        SecurityContextHolder.getContext().setAuthentication(authToken);
        log.info("User {} authenticated successfully", username);
      }
    }
    filterChain.doFilter(request, response);
  }
}
