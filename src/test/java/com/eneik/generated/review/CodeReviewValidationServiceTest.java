package com.eneik.generated.review;

import com.eneik.generated.review.dto.CodeReviewRequest;
import com.eneik.generated.review.dto.CodeReviewResponse;
import com.eneik.generated.review.service.CodeReviewValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CodeReviewValidationServiceTest {

    private CodeReviewValidationService service;

    @BeforeEach
    void setUp() {
        service = new CodeReviewValidationService();
    }

    @Test
    @DisplayName("Should accept approval review when substantive criticalReason is provided")
    void testApproveWithSubstantiveCriticalReason() {
        CodeReviewRequest request = new CodeReviewRequest(
                "PR-101",
                "approve",
                "Code follows design specs, proper error handling implemented, and all boundary conditions checked.",
                List.of()
        );

        CodeReviewResponse response = service.processAndValidateReview(request);

        assertNotNull(response);
        assertEquals("PR-101", response.getPrId());
        assertEquals("approve", response.getVerdict());
        assertEquals("RECORDED", response.getStatus());
        assertNotNull(response.getReviewId());
        assertNotNull(response.getCreatedAt());
    }

    @ParameterizedTest
    @ValueSource(strings = {"approve", "APPROVE", "Approved"})
    @DisplayName("Should reject approval review when criticalReason is empty or blank")
    void testApproveWithEmptyOrBlankCriticalReason(String verdict) {
        CodeReviewRequest emptyReasonRequest = new CodeReviewRequest(
                "PR-102",
                verdict,
                "",
                List.of()
        );

        CodeReviewRequest whitespaceReasonRequest = new CodeReviewRequest(
                "PR-102",
                verdict,
                "   \t \n ",
                List.of()
        );

        CodeReviewRequest nullReasonRequest = new CodeReviewRequest(
                "PR-102",
                verdict,
                null,
                List.of()
        );

        IllegalArgumentException ex1 = assertThrows(
                IllegalArgumentException.class,
                () -> service.processAndValidateReview(emptyReasonRequest)
        );
        assertEquals("Approval code reviews require a substantive criticalReason. Empty or faked reviews are rejected.", ex1.getMessage());

        IllegalArgumentException ex2 = assertThrows(
                IllegalArgumentException.class,
                () -> service.processAndValidateReview(whitespaceReasonRequest)
        );
        assertEquals("Approval code reviews require a substantive criticalReason. Empty or faked reviews are rejected.", ex2.getMessage());

        IllegalArgumentException ex3 = assertThrows(
                IllegalArgumentException.class,
                () -> service.processAndValidateReview(nullReasonRequest)
        );
        assertEquals("Approval code reviews require a substantive criticalReason. Empty or faked reviews are rejected.", ex3.getMessage());
    }

    @Test
    @DisplayName("Should allow non-approval reviews (e.g. block/reject) even without criticalReason")
    void testBlockReviewWithoutCriticalReason() {
        CodeReviewRequest request = new CodeReviewRequest(
                "PR-103",
                "block",
                "",
                List.of("PR violates boundary restrictions by committing generated image artifacts.")
        );

        CodeReviewResponse response = service.processAndValidateReview(request);

        assertNotNull(response);
        assertEquals("PR-103", response.getPrId());
        assertEquals("block", response.getVerdict());
        assertEquals("RECORDED", response.getStatus());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when request or verdict is null/empty")
    void testNullOrEmptyVerdict() {
        assertThrows(IllegalArgumentException.class, () -> service.processAndValidateReview(null));

        CodeReviewRequest noVerdictRequest = new CodeReviewRequest("PR-104", "", "Reason", List.of());
        assertThrows(IllegalArgumentException.class, () -> service.processAndValidateReview(noVerdictRequest));
    }
}
