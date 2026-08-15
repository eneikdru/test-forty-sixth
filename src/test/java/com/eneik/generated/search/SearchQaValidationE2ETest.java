package com.eneik.generated.search;

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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
class SearchQaValidationE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EpidemiologicalMaterialRepository repository;

    private EpidemiologicalMaterial targetMaterial;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        // Target material containing keyword 'influenza'
        targetMaterial = repository.save(new EpidemiologicalMaterial(
                "Influenza Surveillance Protocol 2026",
                "VIRUS",
                "Guidance on seasonal influenza monitoring and reporting protocols."
        ));

        // Distractor materials
        repository.save(new EpidemiologicalMaterial(
                "Cholera Outbreak Control Manual",
                "BACTERIA",
                "Comprehensive measures for waterborne cholera containment."
        ));

        repository.save(new EpidemiologicalMaterial(
                "Ebola Hemorrhagic Fever Response",
                "VIRUS",
                "Emergency response plan and quarantine guidelines for viral outbreaks."
        ));

        repository.save(new EpidemiologicalMaterial(
                "Malaria Prevention and Vector Control",
                "PARASITE",
                "Strategies for mosquito control and parasite containment."
        ));
    }

    /**
     * AC 1: Given an E2E test, When searching for a known keyword,
     * Then the correct material is in the top 3 results.
     */
    @Test
    void testKeywordSearch_TargetMaterialInTop3Results() throws Exception {
        mockMvc.perform(get("/api/v1/materials/search")
                        .param("query", "influenza")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(targetMaterial.getId().intValue())))
                .andExpect(jsonPath("$.content[0].title", is("Influenza Surveillance Protocol 2026")));
    }

    /**
     * AC 2: Given an E2E test, When viewing a material,
     * Then the download link successfully retrieves the file.
     */
    @Test
    void testViewMaterial_DownloadLinkSuccessfullyRetrievesFile() throws Exception {
        mockMvc.perform(get("/api/v1/materials/" + targetMaterial.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(targetMaterial.getId().intValue())))
                .andExpect(jsonPath("$.downloadUrl", notNullValue()))
                .andExpect(jsonPath("$.downloadUrl", is("https://api.epidemiology.example.com/api/v1/materials/" + targetMaterial.getId() + "/download")));
    }
}
