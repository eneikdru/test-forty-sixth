package com.eneik.generated.controller;

import com.eneik.generated.dto.BatchTelemetryEventResponse;
import com.eneik.generated.dto.ErrorResponse;
import com.eneik.generated.dto.MonthlyMetricDto;
import com.eneik.generated.dto.TelemetryEventRequest;
import com.eneik.generated.dto.TelemetryEventResponse;
import com.eneik.generated.service.TelemetryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/telemetry")
public class TelemetryController {

    private final TelemetryService telemetryService;

    public TelemetryController(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @PostMapping("/events")
    public ResponseEntity<TelemetryEventResponse> trackEvent(@RequestBody TelemetryEventRequest request) {
        TelemetryEventResponse response = telemetryService.trackEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/events/batch")
    public ResponseEntity<BatchTelemetryEventResponse> trackBatchEvents(@RequestBody List<TelemetryEventRequest> batch) {
        if (batch == null) {
            throw new IllegalArgumentException("Batch payload cannot be null.");
        }
        telemetryService.processBatchEventsAsync(batch);
        BatchTelemetryEventResponse response = new BatchTelemetryEventResponse(batch.size(), "SUCCESS");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/metrics/monthly")
    public ResponseEntity<List<MonthlyMetricDto>> getMonthlyMetrics() {
        List<MonthlyMetricDto> metrics = telemetryService.getMonthlyMetrics();
        return ResponseEntity.ok(metrics);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "INVALID_PAYLOAD",
                ex.getMessage(),
                Instant.now(),
                List.of(ex.getMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
