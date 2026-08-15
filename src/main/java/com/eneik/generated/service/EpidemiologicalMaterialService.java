package com.eneik.generated.service;

import com.eneik.generated.dto.MaterialDetailDto;
import com.eneik.generated.dto.MaterialSummaryDto;
import com.eneik.generated.dto.SearchPaginatedResponseDto;
import com.eneik.generated.entity.EpidemiologicalMaterial;
import com.eneik.generated.repository.EpidemiologicalMaterialRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class EpidemiologicalMaterialService {

    private final EpidemiologicalMaterialRepository repository;

    public EpidemiologicalMaterialService(EpidemiologicalMaterialRepository repository) {
        this.repository = repository;
    }

    public SearchPaginatedResponseDto searchMaterials(String query, String pathogenType, Pageable pageable) {
        Specification<EpidemiologicalMaterial> spec = EpidemiologicalMaterialRepository.buildSearchSpecification(query, pathogenType);
        Page<EpidemiologicalMaterial> pageResult = repository.findAll(spec, pageable);

        List<MaterialSummaryDto> summaries = pageResult.getContent().stream()
                .map(this::mapToSummaryDto)
                .toList();

        return new SearchPaginatedResponseDto(
                summaries,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.isFirst(),
                pageResult.isLast()
        );
    }

    public MaterialDetailDto getMaterialById(Long id) {
        EpidemiologicalMaterial material = repository.findById(id)
                .orElse(null);
        if (material == null) {
            return null;
        }
        return mapToDetailDto(material);
    }

    private MaterialSummaryDto mapToSummaryDto(EpidemiologicalMaterial entity) {
        String downloadUrl = "https://api.epidemiology.example.com/api/v1/materials/" + entity.getId() + "/download";
        return new MaterialSummaryDto(
                entity.getId(),
                entity.getTitle(),
                entity.getPathogenType(),
                entity.getCreatedAt(),
                downloadUrl
        );
    }

    private MaterialDetailDto mapToDetailDto(EpidemiologicalMaterial entity) {
        String downloadUrl = "https://api.epidemiology.example.com/api/v1/materials/" + entity.getId() + "/download";
        return new MaterialDetailDto(
                entity.getId(),
                entity.getTitle(),
                entity.getPathogenType(),
                entity.getContent(),
                entity.getCreatedAt(),
                downloadUrl
        );
    }
}
