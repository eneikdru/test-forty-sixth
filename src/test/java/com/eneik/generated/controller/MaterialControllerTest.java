package com.eneik.generated.controller;

import com.eneik.generated.Application;
import com.eneik.generated.dto.CreateMaterialRequest;
import com.eneik.generated.dto.UnpublishMaterialRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
class MaterialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateMaterialSuccess() throws Exception {
        CreateMaterialRequest request = new CreateMaterialRequest(
                "COVID-26 Containment Guidelines",
                "VIRUS",
                "Strict isolation and PPE required.",
                "{\"author\":\"CDC\"}"
        );

        mockMvc.perform(post("/api/v1/materials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("COVID-26 Containment Guidelines"))
                .andExpect(jsonPath("$.pathogenType").value("VIRUS"))
                .andExpect(jsonPath("$.status").value("PUBLISHED"));
    }

    @Test
    void testCreateMaterialValidationErrorOnMissingTitle() throws Exception {
        CreateMaterialRequest request = new CreateMaterialRequest(
                "",
                "VIRUS",
                "Content without title",
                "{}"
        );

        mockMvc.perform(post("/api/v1/materials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_PAYLOAD"))
                .andExpect(jsonPath("$.details[0]").value("Field 'title' is required"));
    }

    @Test
    void testUnpublishMaterialSuccess() throws Exception {
        // First create a material
        CreateMaterialRequest createReq = new CreateMaterialRequest(
                "Test Protocol for Unpublish",
                "BACTERIA",
                "Protocol details",
                null
        );

        String responseContent = mockMvc.perform(post("/api/v1/materials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(responseContent).get("id").asText();

        // Now unpublish with CONFIRMED state
        UnpublishMaterialRequest unpublishReq = new UnpublishMaterialRequest("CONFIRMED", "Outdated protocol");

        mockMvc.perform(post("/api/v1/materials/" + id + "/unpublish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(unpublishReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.status").value("UNPUBLISHED"));
    }

    @Test
    void testUnpublishMaterialValidationErrorOnMissingConfirmation() throws Exception {
        CreateMaterialRequest createReq = new CreateMaterialRequest(
                "Test Protocol for Invalid Unpublish",
                "PARASITE",
                "Protocol details",
                null
        );

        String responseContent = mockMvc.perform(post("/api/v1/materials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(responseContent).get("id").asText();

        UnpublishMaterialRequest unpublishReq = new UnpublishMaterialRequest("PENDING", "Reason");

        mockMvc.perform(post("/api/v1/materials/" + id + "/unpublish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(unpublishReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_CONFIRMATION_STATE"));
    }

    @Test
    void testGetMaterialByIdAndList() throws Exception {
        CreateMaterialRequest createReq = new CreateMaterialRequest(
                "Cholera Response Protocol",
                "BACTERIA",
                "Water sanitation guidelines.",
                null
        );

        String responseContent = mockMvc.perform(post("/api/v1/materials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(responseContent).get("id").asText();

        mockMvc.perform(get("/api/v1/materials/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Cholera Response Protocol"));

        mockMvc.perform(get("/api/v1/materials"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
