package com.tkt.quizedu.component;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.tkt.quizedu.data.constant.TokenType;
import com.tkt.quizedu.service.jwt.IJwtService;
import com.tkt.quizedu.service.user.CustomUserDetailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "HANDSHAKE-REQUEST")
public class JwtHandshakeInterceptor implements ChannelInterceptor {

  private final IJwtService jwtService;
  private final CustomUserDetailService userInfoService;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor =
        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      String token = accessor.getFirstNativeHeader("Authorization");
      String userEmail = "";
      if (token != null || !token.startsWith("Bearer ")) {
        token = token.substring(7);
        userEmail = jwtService.extractUsername(TokenType.ACCESS_TOKEN, token);
      }

      if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = userInfoService.loadUserByUsername(userEmail);

        if (jwtService.validateToken(TokenType.ACCESS_TOKEN, token, userDetails)) {
          UsernamePasswordAuthenticationToken authToken =
              new UsernamePasswordAuthenticationToken(
                  userDetails, null, userDetails.getAuthorities());

          SecurityContextHolder.getContext().setAuthentication(authToken);
          accessor.setUser(authToken);
          log.info("Handshake Interceptor successfully");
        }
      }
    }
    return message;
  }
}
