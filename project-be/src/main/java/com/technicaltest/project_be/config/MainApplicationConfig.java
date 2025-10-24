package com.technicaltest.project_be.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
    RabbitMQConfig.class,
    WebClientConfig.class
})
@EnableConfigurationProperties
public class MainApplicationConfig {
}