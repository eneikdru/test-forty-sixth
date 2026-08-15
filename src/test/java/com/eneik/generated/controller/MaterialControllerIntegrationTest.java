package com.eneik.generated.controller;

import com.eneik.generated.dto.CreateMaterialRequest;
import com.eneik.generated.dto.PublishMaterialRequest;
import com.eneik.generated.material.Material;
import com.eneik.generated.material.MaterialRepository;
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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
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

    @BeforeEach
    void setUp() {
        materialRepository.deleteAll();
    }

    @Test
    void testPublishMaterial_Success() throws Exception {
        UUID materialId = UUID.randomUUID();
        Material material = new Material(materialId, "Ebola Containment Guidelines", "VIRUS", "Content details for Ebola...", "{}", "DRAFT");
        materialRepository.save(material);

        PublishMaterialRequest request = new PublishMaterialRequest("PUBLISHED", "Approved by Epi Board");

        mockMvc.perform(post("/api/v1/materials/" + materialId + "/publish")
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MATERIAL_NOT_FOUND"));
    }

    @Test
    void testCreateMaterial_MalformedPayload_Returns400() throws Exception {
        CreateMaterialRequest request = new CreateMaterialRequest("", "VIRUS", "", null);

        mockMvc.perform(post("/api/v1/materials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_PAYLOAD"))
                .andExpect(jsonPath("$.details", hasItem(containsString("title"))));
    }
}
