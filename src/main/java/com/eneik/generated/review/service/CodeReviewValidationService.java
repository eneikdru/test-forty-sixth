package com.eneik.generated.review.service;

import com.eneik.generated.review.dto.CodeReviewRequest;
import com.eneik.generated.review.dto.CodeReviewResponse;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Service
public class CodeReviewValidationService {

    private final Clock clock;

    public CodeReviewValidationService() {
        this(Clock.systemUTC());
    }

    public CodeReviewValidationService(Clock clock) {
        this.clock = clock;
    }

    public CodeReviewResponse processAndValidateReview(CodeReviewRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Code review request cannot be null");
        }

        String verdict = request.getVerdict();
        if (verdict == null || verdict.trim().isEmpty()) {
            throw new IllegalArgumentException("Review verdict is required");
        }

        String normalizedVerdict = verdict.trim().toLowerCase();

        // Enforce substantive criticalReason for approval reviews
        if ("approve".equals(normalizedVerdict) || "approved".equals(normalizedVerdict)) {
            String criticalReason = request.getCriticalReason();
            if (criticalReason == null || criticalReason.trim().isEmpty()) {
                throw new IllegalArgumentException("Approval code reviews require a substantive criticalReason. Empty or faked reviews are rejected.");
            }
        }

        String reviewId = UUID.randomUUID().toString();
        Instant createdAt = Instant.now(clock);

        return new CodeReviewResponse(
                reviewId,
                request.getPrId(),
                request.getVerdict(),
                request.getCriticalReason(),
                request.getConcerns(),
                "RECORDED",
                createdAt
        );
    }
}
