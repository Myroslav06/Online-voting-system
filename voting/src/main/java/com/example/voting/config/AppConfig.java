package com.example.voting.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    
    // Демонстрація створення біна через @Bean [cite: 15]
    @Bean
    public String applicationName() {
        return "Online Voting System v1.0";
    }
}