package com.eneik.generated.controller;

import com.eneik.generated.domain.TelemetryEvent;
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
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TelemetryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TelemetryEventRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void testTrackSingleEventValidSearch() throws Exception {
        Map<String, Object> payload = Map.of(
                "query", "flu outbreak",
                "latencyMs", 45,
                "resultCount", 10
        );
        Map<String, Object> request = Map.of(
                "eventType", "SEARCH",
                "timestamp", "2026-08-15T10:00:00Z",
                "payload", payload
        );

        mockMvc.perform(post("/api/v1/telemetry/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eventType", is("SEARCH")))
                .andExpect(jsonPath("$.status", is("RECORDED")))
                .andExpect(jsonPath("$.id").exists());

        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    void testTrackSingleEventInvalidSearchPayloadReturns400() throws Exception {
        Map<String, Object> payload = Map.of(
                "query", "flu outbreak"
                // missing latencyMs and resultCount
        );
        Map<String, Object> request = Map.of(
                "eventType", "SEARCH",
                "timestamp", "2026-08-15T10:00:00Z",
                "payload", payload
        );

        mockMvc.perform(post("/api/v1/telemetry/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("INVALID_PAYLOAD")));

        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    void testTrackBatchEventsWithValidAndMalformedEventsAsync() throws Exception {
        Map<String, Object> validSearchPayload = Map.of(
                "query", "covid-19",
                "latencyMs", 120,
                "resultCount", 5
        );
        Map<String, Object> validPublicationPayload = Map.of(
                "materialId", "00000000-0000-0000-0000-000000000001",
                "title", "New Protocol"
        );
        Map<String, Object> malformedPayload = Map.of(
                "query", "bad search without latency/resultCount"
        );

        List<Map<String, Object>> batch = List.of(
                Map.of(
                        "id", "11111111-1111-1111-1111-111111111111",
                        "eventType", "SEARCH",
                        "timestamp", "2026-08-15T10:00:00Z",
                        "payload", validSearchPayload
                ),
                Map.of(
                        "id", "22222222-2222-2222-2222-222222222222",
                        "eventType", "SEARCH",
                        "timestamp", "2026-08-15T10:05:00Z",
                        "payload", malformedPayload // Malformed event: missing required SEARCH payload fields
                ),
                Map.of(
                        "id", "33333333-3333-3333-3333-333333333333",
                        "eventType", "PUBLICATION",
                        "timestamp", "2026-08-15T10:10:00Z",
                        "payload", validPublicationPayload
                )
        );

        mockMvc.perform(post("/api/v1/telemetry/events/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batch)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.processedCount", is(3)))
                .andExpect(jsonPath("$.status", is("SUCCESS")));

        // Wait for async processing
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            List<TelemetryEvent> events = repository.findAll();
            // 2 valid events should be saved, 1 malformed event discarded silently
            assertThat(events).hasSize(2);
            assertThat(events).extracting(TelemetryEvent::getId)
                    .containsExactlyInAnyOrder(
                            "11111111-1111-1111-1111-111111111111",
                            "33333333-3333-3333-3333-333333333333"
                    );
        });
    }

    @Test
    void testGetMonthlyMetricsEndpoint() throws Exception {
        repository.save(new TelemetryEvent("evt-1", "SEARCH", java.time.OffsetDateTime.parse("2026-08-15T10:00:00Z"), "{}"));
        repository.save(new TelemetryEvent("evt-2", "SEARCH", java.time.OffsetDateTime.parse("2026-08-16T11:00:00Z"), "{}"));
        repository.save(new TelemetryEvent("evt-3", "PUBLICATION", java.time.OffsetDateTime.parse("2026-08-17T12:00:00Z"), "{}"));

        mockMvc.perform(get("/api/v1/telemetry/metrics/monthly"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].eventType", is("PUBLICATION")))
                .andExpect(jsonPath("$[0].count", is(1)))
                .andExpect(jsonPath("$[1].eventType", is("SEARCH")))
                .andExpect(jsonPath("$[1].count", is(2)));
    }
}
