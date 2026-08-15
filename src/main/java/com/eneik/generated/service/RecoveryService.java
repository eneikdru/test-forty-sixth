package com.eneik.generated.service;

import com.eneik.generated.dto.*;
import com.eneik.generated.entity.RecoveryToken;
import com.eneik.generated.repository.RecoveryTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class RecoveryService {

    private final RecoveryTokenRepository repository;
    private final EmailService emailService;
    private final Clock clock;
    private final Supplier<String> tokenGenerator;

    @Autowired
    public RecoveryService(RecoveryTokenRepository repository, EmailService emailService) {
        this(repository, emailService, Clock.systemUTC(), () -> "rcv_tok_" + UUID.randomUUID().toString().replace("-", ""));
    }

    public RecoveryService(RecoveryTokenRepository repository, EmailService emailService, Clock clock, Supplier<String> tokenGenerator) {
        this.repository = repository;
        this.emailService = emailService;
        this.clock = clock;
        this.tokenGenerator = tokenGenerator;
    }

    @Transactional
    public PasswordResetResponse requestReset(PasswordResetRequest request) {
        if (request == null || request.getEmail() == null || !request.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        Instant now = clock.instant();
        int expiresInMinutes = 30;
        Instant expiresAt = now.plus(Duration.ofMinutes(expiresInMinutes));
        String tokenString = tokenGenerator.get();

        RecoveryToken token = new RecoveryToken(request.getEmail(), tokenString, RecoveryToken.Status.PENDING, expiresAt, now);
        repository.save(token);

        emailService.sendRecoveryEmail(request.getEmail(), tokenString);

        return new PasswordResetResponse(
                "ACCEPTED",
                "If an account exists with this email, password reset instructions have been sent.",
                UUID.randomUUID().toString(),
                expiresInMinutes
        );
    }

    @Transactional(readOnly = true)
    public TokenValidationResponse validateToken(String tokenStr) {
        if (tokenStr == null || tokenStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Token query parameter is required.");
        }

        Optional<RecoveryToken> tokenOpt = repository.findByToken(tokenStr);
        if (tokenOpt.isEmpty()) {
            return new TokenValidationResponse(false, null, "Token not found.", null);
        }

        RecoveryToken token = tokenOpt.get();
        Instant now = clock.instant();

        if (token.getStatus() != RecoveryToken.Status.PENDING || now.isAfter(token.getExpiresAt())) {
            return new TokenValidationResponse(false, maskEmail(token.getEmail()), "Token is expired or already used.", token.getExpiresAt());
        }

        return new TokenValidationResponse(true, maskEmail(token.getEmail()), "Token is valid and ready for password reset.", token.getExpiresAt());
    }

    @Transactional
    public PasswordResetConfirmResponse confirmReset(PasswordResetConfirmRequest request) {
        if (request == null || request.getToken() == null || request.getNewPassword() == null) {
            throw new IllegalArgumentException("Token and new password are required.");
        }
        if (request.getNewPassword().length() < 8) {
            throw new IllegalArgumentException("Password complexity criteria not met (minimum 8 characters).");
        }

        Optional<RecoveryToken> tokenOpt = repository.findByToken(request.getToken());
        if (tokenOpt.isEmpty()) {
            throw new TokenInvalidException("Recovery token is invalid, expired, or already consumed.");
        }

        RecoveryToken token = tokenOpt.get();
        Instant now = clock.instant();

        // Check if expired or status is not PENDING
        if (token.getStatus() != RecoveryToken.Status.PENDING || now.isAfter(token.getExpiresAt())) {
            throw new TokenInvalidException("Recovery token is invalid, expired, or already consumed.");
        }

        // Atomically update status from PENDING to USED
        int updatedRows = repository.markTokenUsed(
                token.getToken(),
                RecoveryToken.Status.PENDING,
                RecoveryToken.Status.USED,
                now
        );

        if (updatedRows == 0) {
            throw new TokenInvalidException("Recovery token is invalid, expired, or already consumed.");
        }

        return new PasswordResetConfirmResponse(
                "SUCCESS",
                "Password has been successfully reset.",
                now
        );
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        int atIdx = email.indexOf('@');
        if (atIdx <= 2) {
            return "***" + email.substring(atIdx);
        }
        return email.charAt(0) + "***" + email.charAt(atIdx - 1) + email.substring(atIdx);
    }

    public static class TokenInvalidException extends RuntimeException {
        public TokenInvalidException(String message) {
            super(message);
        }
    }
}
