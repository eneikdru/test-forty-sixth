package com.eneik.generated.telemetry;

import com.fasterxml.jackson.databind.JsonNode;
import com.eneik.generated.domain.TelemetryEvent;
import com.eneik.generated.entity.EpidemiologicalMaterial;
import com.eneik.generated.material.Material;
import com.eneik.generated.material.MaterialRepository;
import com.eneik.generated.repository.EpidemiologicalMaterialRepository;
import com.eneik.generated.repository.TelemetryEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TelemetryJourneyE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TelemetryEventRepository telemetryEventRepository;

    @Autowired
    private EpidemiologicalMaterialRepository epidemiologicalMaterialRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        telemetryEventRepository.deleteAll();
        epidemiologicalMaterialRepository.deleteAll();
        materialRepository.deleteAll();
    }

    /**
     * AC 1: Given an E2E test, When a full search and view journey is completed,
     * Then the correct metrics are found in the event log.
     */
    @Test
    void testSearchAndViewJourney_TelemetryMetricsRecorded() throws Exception {
        // 1. Seed searchable material
        EpidemiologicalMaterial savedMaterial = epidemiologicalMaterialRepository.save(
                new EpidemiologicalMaterial(
                        "Influenza Surveillance Protocol 2026",
                        "VIRUS",
                        "Guidance on seasonal influenza monitoring and reporting protocols."
                )
        );

        // 2. Perform search request
        mockMvc.perform(get("/api/v1/materials/search")
                        .param("query", "influenza")
                        .param("pathogenType", "VIRUS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title", is("Influenza Surveillance Protocol 2026")));

        // 3. Dispatch SEARCH telemetry event
        Map<String, Object> searchTelemetry = Map.of(
                "id", "10000000-0000-0000-0000-000000000001",
                "eventType", "SEARCH",
                "timestamp", "2026-08-15T10:00:00Z",
                "payload", Map.of(
                        "query", "influenza",
                        "latencyMs", 52,
                        "resultCount", 1,
                        "pathogenType", "VIRUS"
                )
        );

        mockMvc.perform(post("/api/v1/telemetry/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchTelemetry)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("RECORDED")));

        // 4. View material details
        mockMvc.perform(get("/api/v1/materials/" + savedMaterial.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Influenza Surveillance Protocol 2026")));

        // 5. Dispatch VIEW telemetry event
        Map<String, Object> viewTelemetry = Map.of(
                "id", "10000000-0000-0000-0000-000000000002",
                "eventType", "VIEW",
                "timestamp", "2026-08-15T10:01:00Z",
                "payload", Map.of(
                        "materialId", savedMaterial.getId(),
                        "title", "Influenza Surveillance Protocol 2026",
                        "durationMs", 1200
                )
        );

        mockMvc.perform(post("/api/v1/telemetry/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(viewTelemetry)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("RECORDED")));

        // 6. Verify event log contents in repository
        List<TelemetryEvent> events = telemetryEventRepository.findAll();
        assertThat(events).hasSize(2);
        assertThat(events).extracting(TelemetryEvent::getEventType)
                .containsExactlyInAnyOrder("SEARCH", "VIEW");

        // 7. Verify aggregated monthly metrics
        mockMvc.perform(get("/api/v1/telemetry/metrics/monthly"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].eventType", is("SEARCH")))
                .andExpect(jsonPath("$[0].count", is(1)))
                .andExpect(jsonPath("$[1].eventType", is("VIEW")))
                .andExpect(jsonPath("$[1].count", is(1)));
    }

    /**
     * AC 2: Given an E2E test, When a material is published,
     * Then the publication counter increments.
     */
    @Test
    void testMaterialPublished_PublicationCounterIncrements() throws Exception {
        // 1. Seed two draft materials
        UUID mat1Id = UUID.fromString("20000000-0000-0000-0000-000000000001");
        UUID mat2Id = UUID.fromString("20000000-0000-0000-0000-000000000002");

        materialRepository.save(new Material(mat1Id, "Ebola Containment Protocol", "VIRUS", "Protocol body...", "{}", "DRAFT"));
        materialRepository.save(new Material(mat2Id, "Cholera Water Treatment Guide", "BACTERIA", "Guide body...", "{}", "DRAFT"));

        // Initial monthly metrics should be empty
        mockMvc.perform(get("/api/v1/telemetry/metrics/monthly"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        // 2. Publish material 1
        mockMvc.perform(post("/api/v1/materials/" + mat1Id + "/publish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "status", "PUBLISHED",
                                "comment", "Approved for distribution"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("PUBLISHED")));

        // Record publication telemetry event for material 1
        Map<String, Object> pub1Telemetry = Map.of(
                "id", "30000000-0000-0000-0000-000000000001",
                "eventType", "PUBLICATION",
                "timestamp", "2026-08-15T11:00:00Z",
                "payload", Map.of(
                        "materialId", mat1Id.toString(),
                        "title", "Ebola Containment Protocol",
                        "pathogenType", "VIRUS",
                        "action", "PUBLISH"
                )
        );

        mockMvc.perform(post("/api/v1/telemetry/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pub1Telemetry)))
                .andExpect(status().isCreated());

        // Verify metrics shows PUBLICATION count = 1
        mockMvc.perform(get("/api/v1/telemetry/metrics/monthly"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].eventType", is("PUBLICATION")))
                .andExpect(jsonPath("$[0].count", is(1)));

        // 3. Publish material 2
        mockMvc.perform(post("/api/v1/materials/" + mat2Id + "/publish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "status", "PUBLISHED",
                                "comment", "Approved for distribution"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("PUBLISHED")));

        // Record publication telemetry event for material 2
        Map<String, Object> pub2Telemetry = Map.of(
                "id", "30000000-0000-0000-0000-000000000002",
                "eventType", "PUBLICATION",
                "timestamp", "2026-08-15T11:05:00Z",
                "payload", Map.of(
                        "materialId", mat2Id.toString(),
                        "title", "Cholera Water Treatment Guide",
                        "pathogenType", "BACTERIA",
                        "action", "PUBLISH"
                )
        );

        mockMvc.perform(post("/api/v1/telemetry/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pub2Telemetry)))
                .andExpect(status().isCreated());

        // Verify metrics counter incremented to 2
        mockMvc.perform(get("/api/v1/telemetry/metrics/monthly"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].eventType", is("PUBLICATION")))
                .andExpect(jsonPath("$[0].count", is(2)));
    }

    /**
     * Verify batch telemetry ingestion preserves journey metrics across multiple event types.
     */
    @Test
    void testSearchDefectCategorization_AssignsRootCausePatternId() throws Exception {
        Map<String, Object> defectTelemetry = Map.of(
                "id", "50000000-0000-0000-0000-000000000001",
                "eventType", "SEARCH",
                "timestamp", "2026-08-15T13:00:00Z",
                "payload", Map.of(
                        "query", "nonexistent protocol",
                        "latencyMs", 10,
                        "resultCount", 0
                )
        );

        mockMvc.perform(post("/api/v1/telemetry/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(defectTelemetry)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("RECORDED")));

        List<TelemetryEvent> events = telemetryEventRepository.findAll();
        assertThat(events).hasSize(1);
        TelemetryEvent event = events.get(0);
        assertThat(event.getEventType()).isEqualTo("SEARCH");

        JsonNode payloadNode = objectMapper.readTree(event.getPayload());
        assertThat(payloadNode.has("rootCausePatternId")).isTrue();
        assertThat(payloadNode.get("rootCausePatternId").asText()).isEqualTo("UNCATEGORIZED");
    }

    @Test
    void testBatchTelemetryIngestion_IncrementsEventLogMetrics() throws Exception {
        List<Map<String, Object>> batch = List.of(
                Map.of(
                        "id", "40000000-0000-0000-0000-000000000001",
                        "eventType", "SEARCH",
                        "timestamp", "2026-08-15T12:00:00Z",
                        "payload", Map.of("query", "cholera", "latencyMs", 30, "resultCount", 2)
                ),
                Map.of(
                        "id", "40000000-0000-0000-0000-000000000002",
                        "eventType", "PUBLICATION",
                        "timestamp", "2026-08-15T12:05:00Z",
                        "payload", Map.of("materialId", "20000000-0000-0000-0000-000000000001")
                )
        );

        mockMvc.perform(post("/api/v1/telemetry/events/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batch)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.processedCount", is(2)))
                .andExpect(jsonPath("$.status", is("SUCCESS")));

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            List<TelemetryEvent> events = telemetryEventRepository.findAll();
            assertThat(events).hasSize(2);
        });
    }
}
