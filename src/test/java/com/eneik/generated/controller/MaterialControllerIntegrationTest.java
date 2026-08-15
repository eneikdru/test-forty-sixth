package com.eneik.generated.controller;

import com.eneik.generated.dto.CreateMaterialRequest;
import com.eneik.generated.dto.LoginRequest;
import com.eneik.generated.dto.LoginResponse;
import com.eneik.generated.dto.PublishMaterialRequest;
import com.eneik.generated.dto.UnpublishMaterialRequest;
import com.eneik.generated.material.Material;
import com.eneik.generated.material.MaterialRepository;
import com.eneik.generated.service.SessionAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MaterialControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SessionAuthService sessionAuthService;

    private String authToken;

    @BeforeEach
    void setUp() {
        materialRepository.deleteAll();
        sessionAuthService.clearAllSessions();
        LoginResponse response = sessionAuthService.authenticate(new LoginRequest("testuser", "Password123")).orElseThrow();
        authToken = "Bearer " + response.getToken();
    }

    @Test
    void testPublishMaterial_Success() throws Exception {
        UUID materialId = UUID.randomUUID();
        Material material = new Material(materialId, "Ebola Containment Guidelines", "VIRUS", "Content details for Ebola...", "{}", "DRAFT");
        materialRepository.save(material);

        PublishMaterialRequest request = new PublishMaterialRequest("PUBLISHED", "Approved by Epi Board");

        mockMvc.perform(post("/api/v1/materials/" + materialId + "/publish")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(materialId.toString()))
                .andExpect(jsonPath("$.title").value("Ebola Containment Guidelines"))
                .andExpect(jsonPath("$.pathogenType").value("VIRUS"))
                .andExpect(jsonPath("$.status").value("PUBLISHED"));

        Material updated = materialRepository.findById(materialId).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo("PUBLISHED");
    }

    @Test
    void testPublishMaterial_MalformedPayload_Returns400WithSpecificFieldErrors() throws Exception {
        UUID materialId = UUID.randomUUID();
        Material material = new Material(materialId, "Dengue Surveillance", "VIRUS", "Content for Dengue", "{}", "DRAFT");
        materialRepository.save(material);

        // Payload missing status or invalid status value
        PublishMaterialRequest request = new PublishMaterialRequest("INVALID_STATUS", "Comment");

        mockMvc.perform(post("/api/v1/materials/" + materialId + "/publish")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_PAYLOAD"))
                .andExpect(jsonPath("$.details", hasItem(containsString("status"))));
    }

    @Test
    void testPublishMaterial_NotFound_Returns404() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        PublishMaterialRequest request = new PublishMaterialRequest("PUBLISHED", "Note");

        mockMvc.perform(post("/api/v1/materials/" + nonExistentId + "/publish")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MATERIAL_NOT_FOUND"));
    }

    @Test
    void testCreateMaterial_MalformedPayload_Returns400() throws Exception {
        CreateMaterialRequest request = new CreateMaterialRequest("", "VIRUS", "", null);

        mockMvc.perform(post("/api/v1/materials")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_PAYLOAD"))
                .andExpect(jsonPath("$.details", hasItem(containsString("title"))));
    }

    @Test
    void testCreateStructuredDatasetMaterial_Success() throws Exception {
        String structuredJsonContent = "{\"metrics\":{\"cases\":1250,\"R0\":2.4,\"mortality_rate\":0.032},\"surveillance_period\":\"2026-Q1\"}";
        CreateMaterialRequest request = new CreateMaterialRequest(
                "COVID-19 Q1 Surveillance Dataset",
                "VIRUS",
                "EPIDEMIOLOGICAL_DATASET",
                structuredJsonContent,
                "{\"region\":\"Region-A\",\"dataQuality\":\"VERIFIED\"}"
        );

        mockMvc.perform(post("/api/v1/materials")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("COVID-19 Q1 Surveillance Dataset"))
                .andExpect(jsonPath("$.pathogenType").value("VIRUS"))
                .andExpect(jsonPath("$.datasetType").value("EPIDEMIOLOGICAL_DATASET"))
                .andExpect(jsonPath("$.content").value(structuredJsonContent))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void testQueryMaterials_ReturnsStructuredDatasetsAndTextualMaterials() throws Exception {
        // Save textual material
        Material textualMaterial = new Material(
                UUID.randomUUID(),
                "Influenza Outbreak Guidelines",
                "VIRUS",
                null,
                "Standard operational procedures for outbreak containment.",
                "{\"author\":\"EpiTeam\"}",
                "PUBLISHED"
        );
        materialRepository.save(textualMaterial);

        // Save structured dataset material
        Material datasetMaterial = new Material(
                UUID.randomUUID(),
                "Cholera Surveillance Dataset 2026",
                "BACTERIA",
                "EPIDEMIOLOGICAL_DATASET",
                "{\"metrics\":{\"cases\":430,\"activeHotspots\":12}}",
                "{\"format\":\"JSON\"}",
                "PUBLISHED"
        );
        materialRepository.save(datasetMaterial);

        mockMvc.perform(get("/api/v1/materials"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].title", containsInAnyOrder(
                        "Influenza Outbreak Guidelines",
                        "Cholera Surveillance Dataset 2026"
                )))
                .andExpect(jsonPath("$[*].datasetType", hasItem("EPIDEMIOLOGICAL_DATASET")));
    }

    @Test
    void testUnpublishMaterial_Success() throws Exception {
        UUID materialId = UUID.randomUUID();
        Material material = new Material(materialId, "Zika Virus Study", "VIRUS", "Content on Zika...", "{}", "PUBLISHED");
        materialRepository.save(material);

        UnpublishMaterialRequest request = new UnpublishMaterialRequest("CONFIRMED", "Superseded by 2026 study");

        mockMvc.perform(post("/api/v1/materials/" + materialId + "/unpublish")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(materialId.toString()))
                .andExpect(jsonPath("$.title").value("Zika Virus Study"))
                .andExpect(jsonPath("$.status").value("UNPUBLISHED"));

        Material updated = materialRepository.findById(materialId).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo("UNPUBLISHED");
    }

    @Test
    void testUnpublishMaterial_InvalidConfirmationState_Returns400() throws Exception {
        UUID materialId = UUID.randomUUID();
        Material material = new Material(materialId, "Malaria Field Manual", "PARASITE", "Manual details", "{}", "PUBLISHED");
        materialRepository.save(material);

        UnpublishMaterialRequest request = new UnpublishMaterialRequest("UNCONFIRMED", "Reason");

        mockMvc.perform(post("/api/v1/materials/" + materialId + "/unpublish")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_PAYLOAD"))
                .andExpect(jsonPath("$.details", hasItem(containsString("confirmationState"))));
    }

    @Test
    void testUnpublishMaterial_NotPublishedState_Returns400() throws Exception {
        UUID materialId = UUID.randomUUID();
        Material material = new Material(materialId, "Draft Anthrax Protocol", "BACTERIA", "Draft details", "{}", "DRAFT");
        materialRepository.save(material);

        UnpublishMaterialRequest request = new UnpublishMaterialRequest("CONFIRMED", "Retracting draft");

        mockMvc.perform(post("/api/v1/materials/" + materialId + "/unpublish")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("STATE_TRANSITION_FAILED"));
    }

    @Test
    void testGetMaterialById_Success() throws Exception {
        UUID materialId = UUID.randomUUID();
        Material material = new Material(materialId, "Rabies Outbreak Map", "VIRUS", "Map dataset", "{}", "PUBLISHED");
        materialRepository.save(material);

        mockMvc.perform(get("/api/v1/materials/" + materialId)
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(materialId.toString()))
                .andExpect(jsonPath("$.title").value("Rabies Outbreak Map"))
                .andExpect(jsonPath("$.pathogenType").value("VIRUS"));
    }

    @Test
    void testGetMaterialById_NotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/materials/" + nonExistentId)
                        .header("Authorization", authToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MATERIAL_NOT_FOUND"));
    }
}
