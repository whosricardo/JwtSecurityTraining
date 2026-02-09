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
import com.whosricardo.jwtsecuritytraining.dto.LoginResponse;
import com.whosricardo.jwtsecuritytraining.dto.RefreshTokenRequest;
import com.whosricardo.jwtsecuritytraining.dto.RegisterRequest;
import com.whosricardo.jwtsecuritytraining.entity.RefreshToken;
import com.whosricardo.jwtsecuritytraining.entity.User;
import com.whosricardo.jwtsecuritytraining.security.JwtUtil;
import com.whosricardo.jwtsecuritytraining.service.RefreshTokenService;
import com.whosricardo.jwtsecuritytraining.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public AuthController(JwtUtil jwtUtil, UserService userService, AuthenticationManager authenticationManager,
            RefreshTokenService refreshTokenService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public String registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        userService.registerUser(registerRequest);
        return "User registered";
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken uPasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequest.username(), loginRequest.password());
        try {
            authenticationManager.authenticate(uPasswordAuthenticationToken);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        User user = userService.findUserByUsername(loginRequest.username());

        String accessToken = jwtUtil.generateToken(user.getUsername());
        RefreshToken rToken = refreshTokenService.createRefreshToken(user);

        return new LoginResponse(accessToken, rToken.getToken());
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        RefreshToken rToken = refreshTokenService.findByToken(refreshTokenRequest.refreshToken());
        User user = rToken.getUser();
        String newAcessToken = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(newAcessToken);
    }

    @PostMapping("/logout")
    public String logoutUser(@Valid @RequestBody RefreshTokenRequest rTokenRequest) {
        RefreshToken rToken = refreshTokenService.findByToken(rTokenRequest.refreshToken());
        refreshTokenService.deleteRefreshToken(rToken.getToken());
        return "Token deleted";
    }

}
