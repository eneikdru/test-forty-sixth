package com.eneik.generated.service;

import com.eneik.generated.domain.TelemetryEvent;
import com.eneik.generated.dto.MonthlyMetricDto;
import com.eneik.generated.dto.TelemetryEventRequest;
import com.eneik.generated.dto.TelemetryEventResponse;
import com.eneik.generated.repository.TelemetryEventRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TelemetryService {

    private final TelemetryEventRepository repository;

    public TelemetryService(TelemetryEventRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TelemetryEventResponse trackEvent(TelemetryEventRequest request) {
        validateEventRequest(request);

        String id = request.getId();
        if (id == null || id.trim().isEmpty()) {
            id = java.util.UUID.randomUUID().toString();
        }

        TelemetryEvent entity = new TelemetryEvent(
                id,
                request.getEventType(),
                request.getTimestamp(),
                request.getPayload().toString()
        );

        repository.save(entity);

        return new TelemetryEventResponse(
                id,
                request.getEventType(),
                request.getTimestamp(),
                "RECORDED"
        );
    }

    @Async
    public void processBatchEventsAsync(List<TelemetryEventRequest> batch) {
        if (batch == null) {
            return;
        }

        for (TelemetryEventRequest request : batch) {
            try {
                validateEventRequest(request);

                String id = request.getId();
                if (id == null || id.trim().isEmpty()) {
                    id = java.util.UUID.randomUUID().toString();
                }

                TelemetryEvent entity = new TelemetryEvent(
                        id,
                        request.getEventType(),
                        request.getTimestamp(),
                        request.getPayload().toString()
                );

                repository.save(entity);
            } catch (Exception e) {
                // Discard malformed event without crashing the service or failing the batch
            }
        }
    }

    public List<MonthlyMetricDto> getMonthlyMetrics() {
        return repository.aggregateMonthlyMetrics();
    }

    public void validateEventRequest(TelemetryEventRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Telemetry event request cannot be null.");
        }
        if (request.getEventType() == null || request.getEventType().trim().isEmpty()) {
            throw new IllegalArgumentException("eventType is required.");
        }
        if (request.getTimestamp() == null) {
            throw new IllegalArgumentException("timestamp is required.");
        }
        if (request.getPayload() == null || request.getPayload().isNull()) {
            throw new IllegalArgumentException("payload is required.");
        }

        String eventType = request.getEventType().trim().toUpperCase();
        JsonNode payload = request.getPayload();

        if ("SEARCH".equals(eventType)) {
            boolean hasLatency = payload.has("latencyMs") || payload.has("latency");
            boolean hasResultCount = payload.has("resultCount");
            if (!hasLatency || !hasResultCount) {
                throw new IllegalArgumentException("SEARCH event requires latency/latencyMs and resultCount fields.");
            }

            int resultCount = payload.get("resultCount").asInt();
            if (resultCount == 0 && !payload.has("rootCausePatternId")) {
                ((com.fasterxml.jackson.databind.node.ObjectNode) payload).put("rootCausePatternId", "UNCATEGORIZED");
            }
        } else if ("PUBLICATION".equals(eventType)) {
            if (!payload.has("materialId") || payload.get("materialId").isNull() || payload.get("materialId").asText().trim().isEmpty()) {
                throw new IllegalArgumentException("PUBLICATION event requires materialId field.");
            }
        }
    }
}
