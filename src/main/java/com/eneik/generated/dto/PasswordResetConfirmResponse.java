package com.eneik.generated.dto;

import java.time.Instant;

public class PasswordResetConfirmResponse {
    private String status;
    private String message;
    private Instant updatedAt;

    public PasswordResetConfirmResponse() {
    }

    public PasswordResetConfirmResponse(String status, String message, Instant updatedAt) {
        this.status = status;
        this.message = message;
        this.updatedAt = updatedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
