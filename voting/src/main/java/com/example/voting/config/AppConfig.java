package com.example.voting.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    
    @Bean
    public String applicationName() {
        return "Online Voting System v1.0";
    }
}