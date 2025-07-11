package com.tkt.quizedu.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookiesUtils {

  public static void createCookie(
      String name, String value, int maxAge, String path, HttpServletResponse response) {
    Cookie cookie = new Cookie(name, value);
    cookie.setMaxAge(maxAge);
    cookie.setPath(path);
    cookie.setHttpOnly(true);
    cookie.setSecure(true); // Set to true if using HTTPS
    response.addCookie(cookie);
  }

  public static String getRefreshTokenFromCookies(HttpServletRequest request, String name) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if (name.equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }
}
