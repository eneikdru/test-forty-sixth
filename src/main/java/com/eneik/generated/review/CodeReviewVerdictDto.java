package com.eneik.generated.review;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class CodeReviewVerdictDto {

    private Integer sourceIndex;

    @NotNull(message = "verdict must not be null")
    private String verdict;

    private String criticalReason;

    private List<String> concerns = new ArrayList<>();

    public CodeReviewVerdictDto() {
    }

    public CodeReviewVerdictDto(Integer sourceIndex, String verdict, String criticalReason, List<String> concerns) {
        this.sourceIndex = sourceIndex;
        this.verdict = verdict;
        this.criticalReason = criticalReason;
        this.concerns = concerns != null ? concerns : new ArrayList<>();
    }

    public Integer getSourceIndex() {
        return sourceIndex;
    }

    public void setSourceIndex(Integer sourceIndex) {
        this.sourceIndex = sourceIndex;
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
        this.concerns = concerns != null ? concerns : new ArrayList<>();
    }
}
