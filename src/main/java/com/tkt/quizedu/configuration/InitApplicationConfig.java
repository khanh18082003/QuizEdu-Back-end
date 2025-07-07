package com.tkt.quizedu.configuration;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tkt.quizedu.service.InitApplicationService;

@Configuration
public class InitApplicationConfig {

  @Bean
  ApplicationRunner runner(InitApplicationService service) {
    return args -> service.init();
  }
}
