package com.eneik.generated;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class MaterialManagementOpenApiContractTest {

    private static final String CONTRACT_PATH = "docs/contracts/MaterialManagement.openapi.yaml";

    @Test
    void testMaterialManagementContractFileExists() {
        File contractFile = new File(CONTRACT_PATH);
        assertThat(contractFile)
                .as("OpenAPI contract file should exist at " + CONTRACT_PATH)
                .exists()
                .isFile();
    }

    @Test
    void testCreateMaterialSpecRequiresTitlePathogenTypeAndContent() throws IOException {
        String content = Files.readString(Path.of(CONTRACT_PATH));

        assertThat(content).contains("/api/v1/materials:");
        assertThat(content).contains("CreateMaterialRequest:");

        int schemaIdx = content.indexOf("CreateMaterialRequest:");
        assertThat(schemaIdx).isGreaterThan(-1);

        int nextSchemaIdx = content.indexOf("UnpublishMaterialRequest:", schemaIdx);
        String schemaBlock = content.substring(schemaIdx, nextSchemaIdx > -1 ? nextSchemaIdx : content.length());

        assertThat(schemaBlock)
                .as("Create material request schema must require title, pathogenType, and content fields")
                .contains("- title")
                .contains("- pathogenType")
                .contains("- content");
    }

    @Test
    void testUnpublishMaterialSpecRequiresConfirmationState() throws IOException {
        String content = Files.readString(Path.of(CONTRACT_PATH));

        assertThat(content).contains("/api/v1/materials/{id}/unpublish:");
        assertThat(content).contains("UnpublishMaterialRequest:");

        int schemaIdx = content.indexOf("UnpublishMaterialRequest:");
        assertThat(schemaIdx).isGreaterThan(-1);

        int nextSchemaIdx = content.indexOf("MaterialResponse:", schemaIdx);
        String schemaBlock = content.substring(schemaIdx, nextSchemaIdx > -1 ? nextSchemaIdx : content.length());

        assertThat(schemaBlock)
                .as("Unpublish material request schema must require confirmationState field")
                .contains("- confirmationState");
    }

    @Test
    void testContractContainsErrorResponseAndMaterialResponseSchemas() throws IOException {
        String content = Files.readString(Path.of(CONTRACT_PATH));

        assertThat(content).contains("MaterialResponse:");
        assertThat(content).contains("ErrorResponse:");
    }
}
