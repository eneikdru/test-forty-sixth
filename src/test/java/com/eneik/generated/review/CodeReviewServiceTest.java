package com.eneik.generated.review;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CodeReviewServiceTest {

    private CodeReviewService codeReviewService;
    private Instant fixedInstant;

    @BeforeEach
    void setUp() {
        fixedInstant = Instant.parse("2026-08-15T12:00:00Z");
        Clock fixedClock = Clock.fixed(fixedInstant, ZoneId.of("UTC"));
        codeReviewService = new CodeReviewService(fixedClock);
    }

    @Test
    void testProcessReview_ApprovalWithSubstantiveCriticalReason_Success() {
        CodeReviewVerdictDto verdict = new CodeReviewVerdictDto(
                0,
                "approve",
                "PR #47 properly implements session auth logic and passes contract tests.",
                List.of()
        );
        CodeReviewRequest request = new CodeReviewRequest(47, List.of(verdict));

        CodeReviewResponse response = codeReviewService.processReview(request);

        assertNotNull(response);
        assertNotNull(response.getReviewId());
        assertEquals(47, response.getPrNumber());
        assertEquals("RECORDED", response.getStatus());
        assertEquals(fixedInstant, response.getCreatedAt());
        assertEquals(1, response.getVerdicts().size());
    }

    @Test
    void testProcessReview_ApprovalWithNullCriticalReason_ThrowsException() {
        CodeReviewVerdictDto verdict = new CodeReviewVerdictDto(0, "approve", null, List.of());
        CodeReviewRequest request = new CodeReviewRequest(47, List.of(verdict));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> codeReviewService.processReview(request)
        );

        assertEquals("Approval review requires a substantive criticalReason", exception.getMessage());
    }

    @Test
    void testProcessReview_ApprovalWithEmptyCriticalReason_ThrowsException() {
        CodeReviewVerdictDto verdict = new CodeReviewVerdictDto(0, "approve", "", List.of());
        CodeReviewRequest request = new CodeReviewRequest(47, List.of(verdict));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> codeReviewService.processReview(request)
        );

        assertEquals("Approval review requires a substantive criticalReason", exception.getMessage());
    }

    @Test
    void testProcessReview_ApprovalWithBlankCriticalReason_ThrowsException() {
        CodeReviewVerdictDto verdict = new CodeReviewVerdictDto(0, "APPROVE", "   \t \n ", List.of());
        CodeReviewRequest request = new CodeReviewRequest(47, List.of(verdict));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> codeReviewService.processReview(request)
        );

        assertEquals("Approval review requires a substantive criticalReason", exception.getMessage());
    }

    @Test
    void testProcessReview_NonApprovalVerdict_Success() {
        CodeReviewVerdictDto verdict = new CodeReviewVerdictDto(0, "block", "PR is missing automated tests.", List.of("No tests found"));
        CodeReviewRequest request = new CodeReviewRequest(47, List.of(verdict));

        CodeReviewResponse response = codeReviewService.processReview(request);

        assertNotNull(response);
        assertEquals("RECORDED", response.getStatus());
    }

    @Test
    void testProcessReview_MultipleVerdictsOneFakedApproval_ThrowsException() {
        CodeReviewVerdictDto blockVerdict = new CodeReviewVerdictDto(0, "block", "Refusal criteria violation", List.of("Missing tests"));
        CodeReviewVerdictDto fakedApproval = new CodeReviewVerdictDto(1, "approve", "", List.of());
        CodeReviewRequest request = new CodeReviewRequest(47, List.of(blockVerdict, fakedApproval));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> codeReviewService.processReview(request)
        );

        assertEquals("Approval review requires a substantive criticalReason", exception.getMessage());
    }
}
