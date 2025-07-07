package com.tkt.quizedu.configuration;

import java.util.List;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import lombok.NonNull;

@Configuration
public class LocaleResolver extends AcceptHeaderLocaleResolver implements WebMvcConfigurer {

  @NonNull
  @Override
  public Locale resolveLocale(HttpServletRequest request) {
    String languageHeader = request.getHeader("Accept-Language");

    if (StringUtils.hasLength(languageHeader)) {
      List<Locale> supportedLocales = List.of(new Locale("en"), new Locale("vi"));
      Locale locale = Locale.lookup(Locale.LanguageRange.parse(languageHeader), supportedLocales);
      return (locale != null) ? locale : Locale.ENGLISH; // Default if unsupported
    }
    return Locale.ENGLISH; // Default locale if no header is present
  }

  @Bean
  public MessageSource messageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasename("messages");
    messageSource.setDefaultEncoding("UTF-8");
    messageSource.setCacheSeconds(3600);
    return messageSource;
  }
}
