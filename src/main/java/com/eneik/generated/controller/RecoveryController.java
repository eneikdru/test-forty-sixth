package com.eneik.generated.controller;

import com.eneik.generated.dto.*;
import com.eneik.generated.service.RecoveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.util.Collections;

@RestController
@RequestMapping("/api/v1/auth/recovery")
public class RecoveryController {

    private final RecoveryService recoveryService;
    private final Clock clock;

    @Autowired
    public RecoveryController(RecoveryService recoveryService) {
        this(recoveryService, Clock.systemUTC());
    }

    public RecoveryController(RecoveryService recoveryService, Clock clock) {
        this.recoveryService = recoveryService;
        this.clock = clock;
    }

    @PostMapping("/request")
    public ResponseEntity<?> requestReset(@RequestBody PasswordResetRequest request) {
        try {
            PasswordResetResponse response = recoveryService.requestReset(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ErrorResponse error = new ErrorResponse("INVALID_INPUT", e.getMessage(), clock.instant(), Collections.singletonList(e.getMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("INTERNAL_ERROR", e.getMessage(), clock.instant(), Collections.singletonList(e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestParam(value = "token", required = false) String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                ErrorResponse error = new ErrorResponse("INVALID_TOKEN", "Token query parameter is required.", clock.instant(), Collections.singletonList("Missing token"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            TokenValidationResponse response = recoveryService.validateToken(token);
            if (!response.isValid()) {
                ErrorResponse error = new ErrorResponse("TOKEN_EXPIRED_OR_INVALID", response.getMessage(), clock.instant(), Collections.singletonList(response.getMessage()));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("INTERNAL_ERROR", e.getMessage(), clock.instant(), Collections.singletonList(e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmReset(@RequestBody PasswordResetConfirmRequest request) {
        try {
            PasswordResetConfirmResponse response = recoveryService.confirmReset(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ErrorResponse error = new ErrorResponse("INVALID_INPUT", e.getMessage(), clock.instant(), Collections.singletonList(e.getMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (RecoveryService.TokenInvalidException e) {
            ErrorResponse error = new ErrorResponse("INVALID_TOKEN", e.getMessage(), clock.instant(), Collections.singletonList(e.getMessage()));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("INTERNAL_ERROR", e.getMessage(), clock.instant(), Collections.singletonList(e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
