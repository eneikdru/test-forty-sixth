package com.eneik.generated.review;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class CodeReviewRequest {

    @NotNull(message = "prNumber must not be null")
    private Integer prNumber;

    @NotEmpty(message = "verdicts list must not be empty")
    @Valid
    private List<CodeReviewVerdictDto> verdicts = new ArrayList<>();

    public CodeReviewRequest() {
    }

    public CodeReviewRequest(Integer prNumber, List<CodeReviewVerdictDto> verdicts) {
        this.prNumber = prNumber;
        this.verdicts = verdicts != null ? verdicts : new ArrayList<>();
    }

    public Integer getPrNumber() {
        return prNumber;
    }

    public void setPrNumber(Integer prNumber) {
        this.prNumber = prNumber;
    }

    public List<CodeReviewVerdictDto> getVerdicts() {
        return verdicts;
    }

    public void setVerdicts(List<CodeReviewVerdictDto> verdicts) {
        this.verdicts = verdicts != null ? verdicts : new ArrayList<>();
    }
}
