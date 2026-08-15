package com.eneik.generated.review.dto;

import java.util.List;

public class CodeReviewRequest {
    private String prId;
    private String verdict;
    private String criticalReason;
    private List<String> concerns;

    public CodeReviewRequest() {
    }

    public CodeReviewRequest(String prId, String verdict, String criticalReason, List<String> concerns) {
        this.prId = prId;
        this.verdict = verdict;
        this.criticalReason = criticalReason;
        this.concerns = concerns;
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
}
