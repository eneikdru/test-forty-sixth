package com.eneik.generated.dto;

public class PasswordResetResponse {
    private String status;
    private String message;
    private String requestId;
    private Integer expiresInMinutes;

    public PasswordResetResponse() {
    }

    public PasswordResetResponse(String status, String message, String requestId, Integer expiresInMinutes) {
        this.status = status;
        this.message = message;
        this.requestId = requestId;
        this.expiresInMinutes = expiresInMinutes;
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

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Integer getExpiresInMinutes() {
        return expiresInMinutes;
    }

    public void setExpiresInMinutes(Integer expiresInMinutes) {
        this.expiresInMinutes = expiresInMinutes;
    }
}
