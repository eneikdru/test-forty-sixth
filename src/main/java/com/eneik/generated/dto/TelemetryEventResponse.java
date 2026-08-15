package com.eneik.generated.dto;

import java.time.OffsetDateTime;

public class TelemetryEventResponse {

    private String id;
    private String eventType;
    private OffsetDateTime timestamp;
    private String status;

    public TelemetryEventResponse() {
    }

    public TelemetryEventResponse(String id, String eventType, OffsetDateTime timestamp, String status) {
        this.id = id;
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
