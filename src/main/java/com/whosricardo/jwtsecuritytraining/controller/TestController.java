package com.whosricardo.jwtsecuritytraining.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello, your're authenticated";
    }

    @GetMapping("/user")
    public String userEndpoint(Authentication authentication) {
        return "Hello " + authentication.getName() + "! Your role is: " + authentication.getAuthorities();
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminEndpoint() {
        return "Hello admin";
    }
}
