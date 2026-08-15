package com.eneik.generated.controller;

import com.eneik.generated.dto.ErrorResponse;
import com.eneik.generated.dto.LoginRequest;
import com.eneik.generated.dto.LoginResponse;
import com.eneik.generated.service.SessionAuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final SessionAuthService sessionAuthService;
    private final Clock clock;

    @Autowired
    public AuthController(SessionAuthService sessionAuthService) {
        this(sessionAuthService, Clock.systemUTC());
    }

    public AuthController(SessionAuthService sessionAuthService, Clock clock) {
        this.sessionAuthService = sessionAuthService;
        this.clock = clock;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Optional<LoginResponse> response = sessionAuthService.authenticate(request);
        if (response.isEmpty()) {
            ErrorResponse error = new ErrorResponse(
                    "INVALID_CREDENTIALS",
                    "Invalid username or password.",
                    clock.instant(),
                    Collections.singletonList("Authentication failed for user: " + request.getUsername())
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        return ResponseEntity.ok(response.get());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorResponse errorResponse = new ErrorResponse(
                "INVALID_PAYLOAD",
                "Validation failed for login request.",
                clock.instant(),
                details
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
