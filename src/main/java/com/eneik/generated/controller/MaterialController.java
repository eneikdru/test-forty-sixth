package com.eneik.generated.controller;

import com.eneik.generated.dto.CreateMaterialRequest;
import com.eneik.generated.dto.ErrorResponse;
import com.eneik.generated.dto.MaterialResponse;
import com.eneik.generated.dto.UnpublishMaterialRequest;
import com.eneik.generated.entity.EpidemiologicalMaterial;
import com.eneik.generated.repository.EpidemiologicalMaterialRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/materials")
public class MaterialController {

    private final EpidemiologicalMaterialRepository materialRepository;
    private final Map<String, String> statusMap = new ConcurrentHashMap<>();

    public MaterialController(EpidemiologicalMaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    @PostMapping
    public ResponseEntity<?> createMaterial(@RequestBody(required = false) CreateMaterialRequest request) {
        List<String> errors = new ArrayList<>();
        if (request == null) {
            errors.add("Request body is missing");
        } else {
            if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
                errors.add("Field 'title' is required");
            }
            if (request.getPathogenType() == null || request.getPathogenType().trim().isEmpty()) {
                errors.add("Field 'pathogenType' is required");
            }
            if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                errors.add("Field 'content' is required");
            }
        }

        if (!errors.isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse(
                    "INVALID_PAYLOAD",
                    "Invalid request payload or missing required fields.",
                    Instant.now(),
                    errors
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        EpidemiologicalMaterial entity = new EpidemiologicalMaterial(
                request.getTitle(),
                request.getPathogenType(),
                request.getContent()
        );
        EpidemiologicalMaterial saved = materialRepository.save(entity);
        String idStr = String.valueOf(saved.getId());
        statusMap.put(idStr, "PUBLISHED");

        MaterialResponse response = toResponse(saved, "PUBLISHED", request.getMetadata());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MaterialResponse>> listMaterials(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        List<EpidemiologicalMaterial> all = materialRepository.findAll();
        List<MaterialResponse> list = all.stream()
                .map(m -> {
                    String idStr = String.valueOf(m.getId());
                    String status = statusMap.getOrDefault(idStr, "PUBLISHED");
                    return toResponse(m, status, null);
                })
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMaterialById(@PathVariable("id") String id) {
        Long numericId;
        try {
            numericId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            ErrorResponse error = new ErrorResponse("NOT_FOUND", "Material not found.", Instant.now(), List.of("Invalid ID format: " + id));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        Optional<EpidemiologicalMaterial> optional = materialRepository.findById(numericId);
        if (optional.isEmpty()) {
            ErrorResponse error = new ErrorResponse("NOT_FOUND", "Material not found.", Instant.now(), List.of("Material ID " + id + " does not exist."));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        EpidemiologicalMaterial m = optional.get();
        String status = statusMap.getOrDefault(id, "PUBLISHED");
        MaterialResponse response = toResponse(m, status, null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/unpublish")
    public ResponseEntity<?> unpublishMaterial(@PathVariable("id") String id,
                                               @RequestBody(required = false) UnpublishMaterialRequest request) {
        Long numericId;
        try {
            numericId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            ErrorResponse error = new ErrorResponse("NOT_FOUND", "Material not found.", Instant.now(), List.of("Invalid ID format: " + id));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        EpidemiologicalMaterial material = materialRepository.findById(numericId).orElse(null);
        if (material == null) {
            ErrorResponse error = new ErrorResponse("NOT_FOUND", "Material not found.", Instant.now(), List.of("Material ID " + id + " does not exist."));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        if (request == null || !"CONFIRMED".equalsIgnoreCase(request.getConfirmationState())) {
            ErrorResponse error = new ErrorResponse(
                    "INVALID_CONFIRMATION_STATE",
                    "Confirmation state MUST be set to CONFIRMED to unpublish material.",
                    Instant.now(),
                    List.of("Field confirmationState is required and must be CONFIRMED.")
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        statusMap.put(id, "UNPUBLISHED");
        MaterialResponse response = toResponse(material, "UNPUBLISHED", null);
        return ResponseEntity.ok(response);
    }

    private MaterialResponse toResponse(EpidemiologicalMaterial entity, String status, String metadata) {
        Instant createdInstant = entity.getCreatedAt() != null
                ? entity.getCreatedAt().toInstant(ZoneOffset.UTC)
                : Instant.now();

        return new MaterialResponse(
                String.valueOf(entity.getId()),
                entity.getTitle(),
                entity.getPathogenType(),
                entity.getContent(),
                metadata,
                status,
                createdInstant
        );
    }
}
