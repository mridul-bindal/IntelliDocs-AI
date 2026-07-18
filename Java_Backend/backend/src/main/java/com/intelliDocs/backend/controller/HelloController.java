package com.intelliDocs.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String home() {
        return "Hello World! IntelliDocs AI Backend is Running 🚀";
    }

    @GetMapping("/health")
    public String health() {
        return "Backend is Healthy ✅";
    }
}