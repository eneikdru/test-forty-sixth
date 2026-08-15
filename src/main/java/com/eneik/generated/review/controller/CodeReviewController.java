package com.eneik.generated.review.controller;

import com.eneik.generated.dto.ErrorResponse;
import com.eneik.generated.review.dto.CodeReviewRequest;
import com.eneik.generated.review.dto.CodeReviewResponse;
import com.eneik.generated.review.service.CodeReviewValidationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class CodeReviewController {

    private final CodeReviewValidationService reviewValidationService;

    public CodeReviewController(CodeReviewValidationService reviewValidationService) {
        this.reviewValidationService = reviewValidationService;
    }

    @PostMapping
    public ResponseEntity<CodeReviewResponse> submitReview(@RequestBody CodeReviewRequest request) {
        CodeReviewResponse response = reviewValidationService.processAndValidateReview(request);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(
                "INVALID_REVIEW",
                ex.getMessage(),
                Instant.now(),
                List.of(ex.getMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
