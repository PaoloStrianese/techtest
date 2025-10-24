package com.technicaltest.project_be.config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

public class WebClientConfig {

  @Bean
  public WebClient webClient(WebClient.Builder builder) {
    return builder
        .baseUrl("https://gutendex.com")
        .defaultHeader("Authorization", "Bearer API_KEY")
        .build();
  }
}
