package com.whosricardo.jwtsecuritytraining.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.whosricardo.jwtsecuritytraining.entity.RefreshToken;
import com.whosricardo.jwtsecuritytraining.entity.User;
import com.whosricardo.jwtsecuritytraining.exception.TokenExpiredException;
import com.whosricardo.jwtsecuritytraining.exception.TokenNotFoundException;
import com.whosricardo.jwtsecuritytraining.repository.RefreshTokenRepository;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository repository;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMs;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository repository) {
        this.repository = repository;
    }

    public RefreshToken createRefreshToken(User user) {
        repository.deleteByUser(user);

        RefreshToken rToken = new RefreshToken();
        rToken.setToken(UUID.randomUUID().toString());
        rToken.setUser(user);
        rToken.setExpiryDate(Instant.now().plusMillis(refreshExpirationMs));

        return repository.save(rToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            repository.delete(token);
            throw new TokenExpiredException("Token's expired");
        }
        return token;
    }

    public RefreshToken findByToken(String token) {
        RefreshToken rToken = repository.findByToken(token)
                .orElseThrow(() -> new TokenNotFoundException("Token not found"));
        verifyExpiration(rToken);
        return rToken;
    }
}
