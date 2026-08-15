package com.eneik.generated.dto;

import java.time.Instant;

public class UserSessionDto {
    private String token;
    private String username;
    private Instant createdAt;
    private Instant expiresAt;

    public UserSessionDto() {
    }

    public UserSessionDto(String token, String username, Instant createdAt, Instant expiresAt) {
        this.token = token;
        this.username = username;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }
}
