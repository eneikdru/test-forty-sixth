package com.eneik.generated.controller;

import com.eneik.generated.entity.EpidemiologicalMaterial;
import com.eneik.generated.repository.EpidemiologicalMaterialRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/materials")
public class EpidemiologicalMaterialSearchController {

    private final EpidemiologicalMaterialRepository repository;

    public EpidemiologicalMaterialSearchController(EpidemiologicalMaterialRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/search")
    public Page<EpidemiologicalMaterial> searchMaterials(
            @RequestParam(name = "query", required = false) String queryParam,
            @RequestParam(name = "keyword", required = false) String keywordParam,
            @RequestParam(name = "pathogenType", required = false) String pathogenTypeParam,
            @RequestParam(name = "pathogen", required = false) String pathogenParam,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        String keyword = (queryParam != null && !queryParam.isBlank()) ? queryParam : keywordParam;
        String pathogenType = (pathogenTypeParam != null && !pathogenTypeParam.isBlank()) ? pathogenTypeParam : pathogenParam;

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        return repository.searchMaterials(keyword, pathogenType, pageable);
    }
}
