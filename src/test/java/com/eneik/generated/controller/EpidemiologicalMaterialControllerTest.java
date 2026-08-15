package com.eneik.generated.controller;

import com.eneik.generated.Application;
import com.eneik.generated.entity.EpidemiologicalMaterial;
import com.eneik.generated.repository.EpidemiologicalMaterialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
class EpidemiologicalMaterialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EpidemiologicalMaterialRepository repository;

    private EpidemiologicalMaterial mat1;
    private EpidemiologicalMaterial mat2;
    private EpidemiologicalMaterial mat3;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        mat1 = repository.save(new EpidemiologicalMaterial(
                "Influenza Surveillance Protocol 2026",
                "VIRUS",
                "Guidance on seasonal influenza monitoring and reporting protocols."
        ));

        mat2 = repository.save(new EpidemiologicalMaterial(
                "Cholera Outbreak Control Manual",
                "BACTERIA",
                "Comprehensive measures for waterborne cholera containment."
        ));

        mat3 = repository.save(new EpidemiologicalMaterial(
                "Ebola Hemorrhagic Fever Response",
                "VIRUS",
                "Emergency response plan and quarantine guidelines for viral outbreaks."
        ));
    }

    @Test
    void testSearchWithoutFiltersReturnsPaginatedList() throws Exception {
        mockMvc.perform(get("/api/v1/materials/search")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false));
    }

    @Test
    void testSearchByKeyword() throws Exception {
        mockMvc.perform(get("/api/v1/materials/search")
                        .param("query", "outbreak"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].title", containsInAnyOrder(
                        "Cholera Outbreak Control Manual",
                        "Ebola Hemorrhagic Fever Response"
                )));
    }

    @Test
    void testSearchByPathogenTypeFilter() throws Exception {
        mockMvc.perform(get("/api/v1/materials/search")
                        .param("pathogenType", "VIRUS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].title", containsInAnyOrder(
                        "Influenza Surveillance Protocol 2026",
                        "Ebola Hemorrhagic Fever Response"
                )));
    }

    @Test
    void testSearchByKeywordAndPathogenTypeFilter() throws Exception {
        mockMvc.perform(get("/api/v1/materials/search")
                        .param("query", "outbreak")
                        .param("pathogenType", "BACTERIA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title").value("Cholera Outbreak Control Manual"));
    }

    @Test
    void testSearchWithInvalidPathogenTypeReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/materials/search")
                        .param("pathogenType", "UNKNOWN"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_SEARCH_FILTER"))
                .andExpect(jsonPath("$.message").value("The provided pathogen type filter is invalid."));
    }

    @Test
    void testGetMaterialByIdSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/materials/" + mat1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mat1.getId()))
                .andExpect(jsonPath("$.title").value("Influenza Surveillance Protocol 2026"))
                .andExpect(jsonPath("$.pathogenType").value("VIRUS"))
                .andExpect(jsonPath("$.content").value("Guidance on seasonal influenza monitoring and reporting protocols."))
                .andExpect(jsonPath("$.downloadUrl").value("https://api.epidemiology.example.com/api/v1/materials/" + mat1.getId() + "/download"));
    }

    @Test
    void testGetMaterialByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/materials/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MATERIAL_NOT_FOUND"));
    }
}
