package com.whosricardo.jwtsecuritytraining.service;

import com.whosricardo.jwtsecuritytraining.dto.RegisterRequest;
import com.whosricardo.jwtsecuritytraining.entity.User;

public interface UserService {
    User registerUser(RegisterRequest registerRequest);
}
