package com.eneik.generated;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class TelemetryOpenApiContractTest {

    private static final String CONTRACT_PATH = "docs/contracts/Telemetry.openapi.yaml";

    @Test
    void testTelemetryContractFileExists() {
        File contractFile = new File(CONTRACT_PATH);
        assertThat(contractFile)
                .as("OpenAPI contract file should exist at " + CONTRACT_PATH)
                .exists()
                .isFile();
    }

    @Test
    void testSearchEventSpecRequiresLatencyAndResultCount() throws IOException {
        String content = Files.readString(Path.of(CONTRACT_PATH));

        assertThat(content).contains("SearchEventPayload:");
        assertThat(content).contains("/api/v1/telemetry/events:");
        assertThat(content).contains("/api/v1/telemetry/metrics/monthly:");

        // Verify SearchEventPayload required fields
        int searchSchemaIdx = content.indexOf("SearchEventPayload:");
        assertThat(searchSchemaIdx).isGreaterThan(-1);

        String searchSchemaBlock = content.substring(searchSchemaIdx, content.indexOf("SearchTelemetryEvent:", searchSchemaIdx));

        assertThat(searchSchemaBlock)
                .as("Search event schema must require latency and resultCount fields")
                .contains("- resultCount")
                .containsAnyOf("- latencyMs", "- latency");
    }

    @Test
    void testPublicationEventSpecRequiresMaterialId() throws IOException {
        String content = Files.readString(Path.of(CONTRACT_PATH));

        assertThat(content).contains("PublicationEventPayload:");

        // Verify PublicationEventPayload required fields
        int pubSchemaIdx = content.indexOf("PublicationEventPayload:");
        assertThat(pubSchemaIdx).isGreaterThan(-1);

        String pubSchemaBlock = content.substring(pubSchemaIdx, content.indexOf("PublicationTelemetryEvent:", pubSchemaIdx));

        assertThat(pubSchemaBlock)
                .as("Publication event schema must require materialId field")
                .contains("- materialId");
    }

    @Test
    void testContractContainsErrorResponseAndMonthlyMetricSchemas() throws IOException {
        String content = Files.readString(Path.of(CONTRACT_PATH));

        assertThat(content).contains("ErrorResponse:");
        assertThat(content).contains("MonthlyMetricDto:");
        assertThat(content).contains("TelemetryEventRequest:");
        assertThat(content).contains("TelemetryEventResponse:");
    }
}
