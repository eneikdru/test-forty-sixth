package com.eneik.generated.service;

import com.eneik.generated.dto.CodeReviewRequest;
import com.eneik.generated.dto.CodeReviewResponse;
import com.eneik.generated.dto.CodeReviewVerdictDto;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Service
public class CodeReviewService {

    private final Clock clock;

    public CodeReviewService() {
        this(Clock.systemUTC());
    }

    public CodeReviewService(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    public CodeReviewResponse processReview(CodeReviewRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("CodeReviewRequest must not be null");
        }
        if (request.getPrNumber() == null) {
            throw new IllegalArgumentException("prNumber must not be null");
        }
        if (request.getVerdicts() == null || request.getVerdicts().isEmpty()) {
            throw new IllegalArgumentException("verdicts list must not be empty");
        }

        for (CodeReviewVerdictDto verdictDto : request.getVerdicts()) {
            if (verdictDto == null) {
                throw new IllegalArgumentException("verdict entry must not be null");
            }
            String verdict = verdictDto.getVerdict();
            if (verdict == null || verdict.trim().isEmpty()) {
                throw new IllegalArgumentException("verdict string must not be empty");
            }

            if ("approve".equalsIgnoreCase(verdict.trim())) {
                String criticalReason = verdictDto.getCriticalReason();
                if (criticalReason == null || criticalReason.trim().isEmpty()) {
                    throw new IllegalArgumentException("Approval review requires a substantive criticalReason");
                }
            }
        }

        UUID reviewId = UUID.randomUUID();
        Instant now = clock.instant();

        return new CodeReviewResponse(
                reviewId,
                request.getPrNumber(),
                "RECORDED",
                request.getVerdicts(),
                now
        );
    }
}
