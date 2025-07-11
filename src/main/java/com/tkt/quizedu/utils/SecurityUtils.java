package com.tkt.quizedu.utils;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.tkt.quizedu.data.collection.User;

public class SecurityUtils {

  public static Collection<? extends GrantedAuthority> getAuthorities(User user) {
    return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
  }

  public static Collection<? extends GrantedAuthority> getAuthorities(String role) {
    return List.of(new SimpleGrantedAuthority("ROLE_" + role));
  }
}
