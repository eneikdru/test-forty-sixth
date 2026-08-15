package com.eneik.generated.dto;

public class BatchTelemetryEventResponse {

    private int processedCount;
    private String status;

    public BatchTelemetryEventResponse() {
    }

    public BatchTelemetryEventResponse(int processedCount, String status) {
        this.processedCount = processedCount;
        this.status = status;
    }

    public int getProcessedCount() {
        return processedCount;
    }

    public void setProcessedCount(int processedCount) {
        this.processedCount = processedCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
