package com.eneik.generated.dto;

import java.time.Instant;
import java.util.List;

public class ErrorResponse {
    private String code;
    private String message;
    private Instant timestamp;
    private List<String> details;

    public ErrorResponse() {
    }

    public ErrorResponse(String code, String message, Instant timestamp, List<String> details) {
        this.code = code;
        this.message = message;
        this.timestamp = timestamp;
        this.details = details;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }
}
