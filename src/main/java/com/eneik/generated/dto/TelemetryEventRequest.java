package com.eneik.generated.dto;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.OffsetDateTime;

public class TelemetryEventRequest {

    private String id;
    private String eventType;
    private OffsetDateTime timestamp;
    private JsonNode payload;

    public TelemetryEventRequest() {
    }

    public TelemetryEventRequest(String id, String eventType, OffsetDateTime timestamp, JsonNode payload) {
        this.id = id;
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.payload = payload;
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

    public JsonNode getPayload() {
        return payload;
    }

    public void setPayload(JsonNode payload) {
        this.payload = payload;
    }
}
