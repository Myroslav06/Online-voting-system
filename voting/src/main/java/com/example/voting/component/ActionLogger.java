package com.example.voting.component;

import java.time.LocalDateTime;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class ActionLogger {
    public void log(String message) {
        System.out.println("[LOG " + LocalDateTime.now() + "]: " + message + " (Bean Hash: " + this.hashCode() + ")");
    }
}