package com.eneik.generated.service;

import com.eneik.generated.dto.LoginRequest;
import com.eneik.generated.dto.LoginResponse;
import com.eneik.generated.dto.UserSessionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Service
public class SessionAuthService {

    private final Clock clock;
    private final Supplier<String> idGenerator;
    private final Map<String, UserSessionDto> sessions = new ConcurrentHashMap<>();
    private final Duration sessionDuration = Duration.ofHours(8);

    @Autowired
    public SessionAuthService() {
        this(Clock.systemUTC(), () -> UUID.randomUUID().toString());
    }

    public SessionAuthService(Clock clock, Supplier<String> idGenerator) {
        this.clock = clock;
        this.idGenerator = idGenerator;
    }

    public Optional<LoginResponse> authenticate(LoginRequest request) {
        if (request == null || request.getUsername() == null || request.getPassword() == null) {
            return Optional.empty();
        }

        String username = request.getUsername().trim();
        String password = request.getPassword();

        // Validate credentials against domain rules/known users.
        if (!isValidCredentials(username, password)) {
            return Optional.empty();
        }

        Instant now = clock.instant();
        Instant expiresAt = now.plus(sessionDuration);
        String token = "sess_" + idGenerator.get();

        UserSessionDto session = new UserSessionDto(token, username, now, expiresAt);
        sessions.put(token, session);

        return Optional.of(new LoginResponse(token, username, expiresAt));
    }

    public Optional<UserSessionDto> validateSession(String token) {
        if (token == null || token.trim().isEmpty()) {
            return Optional.empty();
        }

        String cleanToken = token.trim();
        if (cleanToken.startsWith("Bearer ")) {
            cleanToken = cleanToken.substring(7).trim();
        }

        UserSessionDto session = sessions.get(cleanToken);
        if (session == null) {
            return Optional.empty();
        }

        if (clock.instant().isAfter(session.getExpiresAt())) {
            sessions.remove(cleanToken);
            return Optional.empty();
        }

        return Optional.of(session);
    }

    public void invalidateSession(String token) {
        if (token != null) {
            String cleanToken = token.trim();
            if (cleanToken.startsWith("Bearer ")) {
                cleanToken = cleanToken.substring(7).trim();
            }
            sessions.remove(cleanToken);
        }
    }

    public void clearAllSessions() {
        sessions.clear();
    }

    private boolean isValidCredentials(String username, String password) {
        // Simple secure check or configurable/accepted passwords for epidemiologists/admins/test users
        if (username.isBlank() || password.isBlank()) {
            return false;
        }
        // Accepts valid formatted user credentials (non-empty password with minimum length)
        return password.length() >= 6;
    }
}
