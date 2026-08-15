package com.eneik.generated.controller;

import com.eneik.generated.dto.ErrorResponse;
import com.eneik.generated.dto.MaterialDetailDto;
import com.eneik.generated.dto.SearchPaginatedResponseDto;
import com.eneik.generated.service.EpidemiologicalMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/materials")
public class EpidemiologicalMaterialController {

    private static final Set<String> ALLOWED_PATHOGEN_TYPES = Set.of("VIRUS", "BACTERIA", "PARASITE", "FUNGI", "OTHER");

    private final EpidemiologicalMaterialService materialService;
    private final Clock clock;

    @Autowired
    public EpidemiologicalMaterialController(EpidemiologicalMaterialService materialService) {
        this(materialService, Clock.systemUTC());
    }

    public EpidemiologicalMaterialController(EpidemiologicalMaterialService materialService, Clock clock) {
        this.materialService = materialService;
        this.clock = clock;
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchMaterials(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "pathogenType", required = false) String pathogenType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "createdAt,desc") String sortParam) {

        if (pathogenType != null && !pathogenType.trim().isEmpty()) {
            String upper = pathogenType.trim().toUpperCase();
            if (!ALLOWED_PATHOGEN_TYPES.contains(upper)) {
                ErrorResponse error = new ErrorResponse(
                        "INVALID_SEARCH_FILTER",
                        "The provided pathogen type filter is invalid.",
                        clock.instant(),
                        List.of("pathogenType must be one of: VIRUS, BACTERIA, PARASITE, FUNGI, OTHER")
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
        }

        if (page < 0 || size < 1 || size > 100) {
            ErrorResponse error = new ErrorResponse(
                    "INVALID_PAGINATION_PARAMETERS",
                    "Page index must be >= 0 and size must be between 1 and 100.",
                    clock.instant(),
                    List.of("Invalid page or size parameter")
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        Sort sort = parseSortParam(sortParam);
        Pageable pageable = PageRequest.of(page, size, sort);

        SearchPaginatedResponseDto response = materialService.searchMaterials(query, pathogenType, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<?> getMaterialById(@PathVariable("id") Long id) {
        if (id == null || id <= 0) {
            ErrorResponse error = new ErrorResponse(
                    "INVALID_MATERIAL_ID",
                    "Material ID must be a positive integer.",
                    clock.instant(),
                    List.of("Invalid ID format")
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        MaterialDetailDto material = materialService.getMaterialById(id);
        if (material == null) {
            ErrorResponse error = new ErrorResponse(
                    "MATERIAL_NOT_FOUND",
                    "Material not found with id: " + id,
                    clock.instant(),
                    List.of("Material absent")
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        return ResponseEntity.ok(material);
    }

    private Sort parseSortParam(String sortParam) {
        if (sortParam == null || sortParam.trim().isEmpty()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        String[] parts = sortParam.split(",");
        String property = parts[0].trim();
        Sort.Direction direction = Sort.Direction.ASC;
        if (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim())) {
            direction = Sort.Direction.DESC;
        }
        return Sort.by(direction, property);
    }
}
