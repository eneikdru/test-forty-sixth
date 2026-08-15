package com.eneik.generated.controller;

import com.eneik.generated.dto.CodeReviewRequest;
import com.eneik.generated.dto.CodeReviewResponse;
import com.eneik.generated.dto.ErrorResponse;
import com.eneik.generated.service.CodeReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/code-reviews")
public class GeneratedCodeReviewController {

    private final CodeReviewService codeReviewService;

    public GeneratedCodeReviewController(CodeReviewService codeReviewService) {
        this.codeReviewService = codeReviewService;
    }

    @PostMapping
    public ResponseEntity<CodeReviewResponse> processReview(@Valid @RequestBody CodeReviewRequest request) {
        CodeReviewResponse response = codeReviewService.processReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorResponse errorResponse = new ErrorResponse(
                "INVALID_PAYLOAD",
                "Validation failed for code review payload.",
                Instant.now(),
                details
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "INVALID_REVIEW",
                ex.getMessage(),
                Instant.now(),
                List.of(ex.getMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
