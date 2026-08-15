package com.eneik.generated.controller;

import com.eneik.generated.Application;
import com.eneik.generated.entity.EpidemiologicalMaterial;
import com.eneik.generated.repository.EpidemiologicalMaterialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
class EpidemiologicalMaterialSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EpidemiologicalMaterialRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        repository.save(new EpidemiologicalMaterial("Influenza Surveillance Protocol 2026", "VIRUS", "Guidance on seasonal influenza monitoring and reporting protocols."));
        repository.save(new EpidemiologicalMaterial("Cholera Outbreak Control Manual", "BACTERIA", "Comprehensive measures for waterborne cholera containment."));
        repository.save(new EpidemiologicalMaterial("Ebola Hemorrhagic Fever Response", "VIRUS", "Emergency response plan and quarantine guidelines for viral outbreaks."));
        repository.save(new EpidemiologicalMaterial("Malaria Prevention and Vector Control", "PARASITE", "Strategies for mosquito control and parasite containment."));
        repository.save(new EpidemiologicalMaterial("Rabies Surveillance and Immunization", "VIRUS", "Protocol for managing viral zoonotic infection transmission."));
    }

    @Test
    void testKeywordSearchWithPagination() throws Exception {
        mockMvc.perform(get("/api/materials/search")
                        .param("query", "outbreak")
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.page.totalElements", is(2)))
                .andExpect(jsonPath("$.page.totalPages", is(2)))
                .andExpect(jsonPath("$.page.number", is(0)));

        mockMvc.perform(get("/api/materials/search")
                        .param("query", "outbreak")
                        .param("page", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.page.number", is(1)));
    }

    @Test
    void testFilterByPathogenType() throws Exception {
        mockMvc.perform(get("/api/materials/search")
                        .param("pathogenType", "VIRUS")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.page.totalElements", is(3)))
                .andExpect(jsonPath("$.content[*].pathogenType", everyItem(is("VIRUS"))));
    }

    @Test
    void testKeywordAndPathogenFilterCombined() throws Exception {
        mockMvc.perform(get("/api/materials/search")
                        .param("query", "protocol")
                        .param("pathogenType", "VIRUS")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.page.totalElements", is(2)));
    }

    @Test
    void testAliasParametersQueryAndPathogen() throws Exception {
        mockMvc.perform(get("/api/materials/search")
                        .param("keyword", "cholera")
                        .param("pathogen", "BACTERIA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title", is("Cholera Outbreak Control Manual")));
    }
}
