package com.eneik.generated.service;

import com.eneik.generated.dto.CreateMaterialRequest;
import com.eneik.generated.dto.MaterialResponse;
import com.eneik.generated.dto.PublishMaterialRequest;
import com.eneik.generated.dto.UnpublishMaterialRequest;
import com.eneik.generated.material.Material;
import com.eneik.generated.material.MaterialRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MaterialService {

    private final MaterialRepository materialRepository;

    public MaterialService(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    @Transactional
    public MaterialResponse createMaterial(CreateMaterialRequest request) {
        UUID id = UUID.randomUUID();
        Material material = new Material(
                id,
                request.getTitle(),
                request.getPathogenType(),
                request.getDatasetType(),
                request.getContent(),
                request.getMetadata(),
                "DRAFT"
        );
        Material saved = materialRepository.save(material);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<MaterialResponse> listMaterials(int page, int size) {
        Page<Material> materialsPage = materialRepository.findAll(PageRequest.of(page, size));
        return materialsPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MaterialResponse getMaterialById(UUID id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Material not found with id: " + id));
        return mapToResponse(material);
    }

    @Transactional
    public MaterialResponse publishMaterial(UUID id, PublishMaterialRequest request) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Material not found with id: " + id));

        // Atomic guarded database update
        int updatedCount = materialRepository.updateStatus(id, "PUBLISHED");
        if (updatedCount == 0) {
            throw new IllegalStateException("Failed to publish material: material state could not be updated.");
        }
        material.setStatus("PUBLISHED");

        return mapToResponse(material);
    }

    @Transactional
    public MaterialResponse unpublishMaterial(UUID id, UnpublishMaterialRequest request) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Material not found with id: " + id));

        if (!"CONFIRMED".equalsIgnoreCase(request.getConfirmationState())) {
            throw new IllegalArgumentException("Confirmation state MUST be set to CONFIRMED to unpublish material.");
        }

        // Atomic guarded database update (only if currently PUBLISHED)
        int updatedCount = materialRepository.updateStatusConditionally(id, "PUBLISHED", "UNPUBLISHED");
        if (updatedCount == 0) {
            throw new IllegalStateException("Cannot unpublish material: material is not in PUBLISHED state.");
        }
        material.setStatus("UNPUBLISHED");

        return mapToResponse(material);
    }

    private MaterialResponse mapToResponse(Material material) {
        return new MaterialResponse(
                material.getId().toString(),
                material.getTitle(),
                material.getPathogenType(),
                material.getDatasetType(),
                material.getContent(),
                material.getMetadata(),
                material.getStatus(),
                material.getCreatedAt()
        );
    }
}
