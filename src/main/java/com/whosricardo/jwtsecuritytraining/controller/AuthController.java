package com.whosricardo.jwtsecuritytraining.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.whosricardo.jwtsecuritytraining.dto.AuthResponse;
import com.whosricardo.jwtsecuritytraining.dto.LoginRequest;
import com.whosricardo.jwtsecuritytraining.dto.RegisterRequest;
import com.whosricardo.jwtsecuritytraining.security.JwtUtil;
import com.whosricardo.jwtsecuritytraining.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(JwtUtil jwtUtil, UserService userService, AuthenticationManager authenticationManager) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public String registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        userService.registerUser(registerRequest);
        return "User registered";
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken uPasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequest.username(), loginRequest.password());
        try {
            authenticationManager.authenticate(uPasswordAuthenticationToken);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        return new AuthResponse(jwtUtil.generateToken(loginRequest.username()));
    }
}
