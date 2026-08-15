package com.eneik.generated.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class CodeReviewResponse {

    private UUID reviewId;
    private Integer prNumber;
    private String status;
    private List<CodeReviewVerdictDto> verdicts;
    private Instant createdAt;

    public CodeReviewResponse() {
    }

    public CodeReviewResponse(UUID reviewId, Integer prNumber, String status, List<CodeReviewVerdictDto> verdicts, Instant createdAt) {
        this.reviewId = reviewId;
        this.prNumber = prNumber;
        this.status = status;
        this.verdicts = verdicts;
        this.createdAt = createdAt;
    }

    public UUID getReviewId() {
        return reviewId;
    }

    public void setReviewId(UUID reviewId) {
        this.reviewId = reviewId;
    }

    public Integer getPrNumber() {
        return prNumber;
    }

    public void setPrNumber(Integer prNumber) {
        this.prNumber = prNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CodeReviewVerdictDto> getVerdicts() {
        return verdicts;
    }

    public void setVerdicts(List<CodeReviewVerdictDto> verdicts) {
        this.verdicts = verdicts;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
