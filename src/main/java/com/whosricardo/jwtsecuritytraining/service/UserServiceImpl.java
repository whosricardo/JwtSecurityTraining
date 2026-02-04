package com.whosricardo.jwtsecuritytraining.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.whosricardo.jwtsecuritytraining.dto.RegisterRequest;
import com.whosricardo.jwtsecuritytraining.entity.User;
import com.whosricardo.jwtsecuritytraining.exception.UserAlreadyExistsException;
import com.whosricardo.jwtsecuritytraining.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.username())) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        User user = new User();
        user.setUsername(registerRequest.username());
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        user.setEmail(registerRequest.email());
        user.setRole("ROLE_USER");

        return userRepository.save(user);
    }
}
