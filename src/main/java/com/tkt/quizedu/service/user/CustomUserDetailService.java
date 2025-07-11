package com.tkt.quizedu.service.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tkt.quizedu.data.collection.CustomUserDetail;
import com.tkt.quizedu.data.collection.User;
import com.tkt.quizedu.data.constant.ErrorCode;
import com.tkt.quizedu.data.repository.UserRepository;
import com.tkt.quizedu.exception.QuizException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user =
        userRepository
            .findByEmail(username)
            .orElseThrow(() -> new QuizException(ErrorCode.MESSAGE_INVALID_ID));
    return new CustomUserDetail(user);
  }
}
