package com.eneik.generated.dto;

import java.time.Instant;

public class TokenValidationResponse {
    private boolean valid;
    private String maskedEmail;
    private String message;
    private Instant expiresAt;

    public TokenValidationResponse() {
    }

    public TokenValidationResponse(boolean valid, String maskedEmail, String message, Instant expiresAt) {
        this.valid = valid;
        this.maskedEmail = maskedEmail;
        this.message = message;
        this.expiresAt = expiresAt;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getMaskedEmail() {
        return maskedEmail;
    }

    public void setMaskedEmail(String maskedEmail) {
        this.maskedEmail = maskedEmail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }
}
