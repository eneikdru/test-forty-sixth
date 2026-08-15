package com.eneik.generated.review.dto;

import java.time.Instant;
import java.util.List;

public class CodeReviewResponse {
    private String reviewId;
    private String prId;
    private String verdict;
    private String criticalReason;
    private List<String> concerns;
    private String status;
    private Instant createdAt;

    public CodeReviewResponse() {
    }

    public CodeReviewResponse(String reviewId, String prId, String verdict, String criticalReason, List<String> concerns, String status, Instant createdAt) {
        this.reviewId = reviewId;
        this.prId = prId;
        this.verdict = verdict;
        this.criticalReason = criticalReason;
        this.concerns = concerns;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getPrId() {
        return prId;
    }

    public void setPrId(String prId) {
        this.prId = prId;
    }

    public String getVerdict() {
        return verdict;
    }

    public void setVerdict(String verdict) {
        this.verdict = verdict;
    }

    public String getCriticalReason() {
        return criticalReason;
    }

    public void setCriticalReason(String criticalReason) {
        this.criticalReason = criticalReason;
    }

    public List<String> getConcerns() {
        return concerns;
    }

    public void setConcerns(List<String> concerns) {
        this.concerns = concerns;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
