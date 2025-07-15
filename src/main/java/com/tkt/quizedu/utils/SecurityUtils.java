package com.tkt.quizedu.utils;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.tkt.quizedu.data.collection.CustomUserDetail;
import com.tkt.quizedu.data.collection.User;

public class SecurityUtils {

  public static Collection<? extends GrantedAuthority> getAuthorities(User user) {
    return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
  }

  public static Collection<? extends GrantedAuthority> getAuthorities(String role) {
    return List.of(new SimpleGrantedAuthority("ROLE_" + role));
  }

  public static CustomUserDetail getUserDetail() {
    return (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }
}
