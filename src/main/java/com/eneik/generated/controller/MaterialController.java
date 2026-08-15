package com.eneik.generated.controller;

import com.eneik.generated.dto.CreateMaterialRequest;
import com.eneik.generated.dto.ErrorResponse;
import com.eneik.generated.dto.MaterialResponse;
import com.eneik.generated.dto.PublishMaterialRequest;
import com.eneik.generated.dto.UnpublishMaterialRequest;
import com.eneik.generated.service.MaterialService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/materials")
public class MaterialController {

    private final MaterialService materialService;

    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @PostMapping
    public ResponseEntity<MaterialResponse> createMaterial(@Valid @RequestBody CreateMaterialRequest request) {
        MaterialResponse response = materialService.createMaterial(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MaterialResponse>> listMaterials(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<MaterialResponse> responses = materialService.listMaterials(page, size);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialResponse> getMaterialById(@PathVariable UUID id) {
        MaterialResponse response = materialService.getMaterialById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<MaterialResponse> publishMaterial(
            @PathVariable UUID id,
            @Valid @RequestBody PublishMaterialRequest request) {
        MaterialResponse response = materialService.publishMaterial(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/unpublish")
    public ResponseEntity<MaterialResponse> unpublishMaterial(
            @PathVariable UUID id,
            @Valid @RequestBody UnpublishMaterialRequest request) {
        MaterialResponse response = materialService.unpublishMaterial(id, request);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorResponse errorResponse = new ErrorResponse(
                "INVALID_PAYLOAD",
                "Validation failed for request payload.",
                Instant.now(),
                details
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "INVALID_REQUEST",
                ex.getMessage(),
                Instant.now(),
                List.of(ex.getMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "STATE_TRANSITION_FAILED",
                ex.getMessage(),
                Instant.now(),
                List.of(ex.getMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NoSuchElementException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "MATERIAL_NOT_FOUND",
                ex.getMessage(),
                Instant.now(),
                List.of(ex.getMessage())
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
