package com.eneik.generated.restore;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class RestoreVerificationTest {

    @Test
    void testRestoreScriptExecutesAndRestoresDataIntact(@TempDir Path tempDir) throws Exception {
        Path sourceDataDir = tempDir.resolve("data");
        Path backupStorageDir = tempDir.resolve("backup-storage");
        Path targetDataDir = tempDir.resolve("restored-data");

        Files.createDirectories(sourceDataDir);
        Files.createDirectories(backupStorageDir);

        Path testFile1 = sourceDataDir.resolve("appdb.mv.db");
        Path testFile2 = sourceDataDir.resolve("protocol_data.json");
        Files.writeString(testFile1, "BINARY_DATABASE_CONTENT_HEADER_12345");
        Files.writeString(testFile2, "{\"protocolId\": \"EP-2026\", \"status\": \"ACTIVE\"}");

        // 1. Run backup.sh
        ProcessBuilder backupPb = new ProcessBuilder("bash", "infrastructure/backup.sh");
        Map<String, String> backupEnv = backupPb.environment();
        backupEnv.put("SOURCE_DATA_DIR", sourceDataDir.toAbsolutePath().toString());
        backupEnv.put("SECURE_STORAGE_DIR", backupStorageDir.toAbsolutePath().toString());

        Process backupProcess = backupPb.start();
        int backupExitCode = backupProcess.waitFor();
        assertThat(backupExitCode).isEqualTo(0);

        File[] backups = backupStorageDir.toFile().listFiles((dir, name) -> name.endsWith(".tar.gz"));
        assertThat(backups).isNotNull().hasSize(1);

        // 2. Run restore.sh
        ProcessBuilder restorePb = new ProcessBuilder("bash", "infrastructure/restore.sh");
        Map<String, String> restoreEnv = restorePb.environment();
        restoreEnv.put("TARGET_DATA_DIR", targetDataDir.toAbsolutePath().toString());
        restoreEnv.put("SECURE_STORAGE_DIR", backupStorageDir.toAbsolutePath().toString());

        Process restoreProcess = restorePb.start();
        String output;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(restoreProcess.getInputStream()))) {
            output = reader.lines().collect(Collectors.joining("\n"));
        }
        int restoreExitCode = restoreProcess.waitFor();

        assertThat(restoreExitCode).isEqualTo(0);
        assertThat(output).contains("Restore completed successfully. RTO measured:");

        // 3. Verify data intact
        assertThat(targetDataDir.resolve("appdb.mv.db")).exists().hasContent("BINARY_DATABASE_CONTENT_HEADER_12345");
        assertThat(targetDataDir.resolve("protocol_data.json")).exists().hasContent("{\"protocolId\": \"EP-2026\", \"status\": \"ACTIVE\"}");
    }

    @Test
    void testRestoreScriptMeasuresAndLogsRTO(@TempDir Path tempDir) throws Exception {
        Path sourceDataDir = tempDir.resolve("data");
        Path backupStorageDir = tempDir.resolve("backup-storage");

        Files.createDirectories(sourceDataDir);
        Files.createDirectories(backupStorageDir);
        Files.writeString(sourceDataDir.resolve("data.txt"), "sample data");

        // Create backup first
        ProcessBuilder backupPb = new ProcessBuilder("bash", "infrastructure/backup.sh");
        backupPb.environment().put("SOURCE_DATA_DIR", sourceDataDir.toAbsolutePath().toString());
        backupPb.environment().put("SECURE_STORAGE_DIR", backupStorageDir.toAbsolutePath().toString());
        backupPb.start().waitFor();

        // Run restore
        ProcessBuilder restorePb = new ProcessBuilder("bash", "infrastructure/restore.sh");
        restorePb.environment().put("TARGET_DATA_DIR", sourceDataDir.toAbsolutePath().toString());
        restorePb.environment().put("SECURE_STORAGE_DIR", backupStorageDir.toAbsolutePath().toString());

        Process restoreProcess = restorePb.start();
        String output;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(restoreProcess.getInputStream()))) {
            output = reader.lines().collect(Collectors.joining("\n"));
        }
        int exitCode = restoreProcess.waitFor();

        assertThat(exitCode).isEqualTo(0);
        assertThat(output).contains("RTO measured:");
        assertThat(output).matches("(?s).*RTO measured: \\d+ms.*");
    }

    @Test
    void testRestoreScriptFailsWhenNoBackupFound(@TempDir Path tempDir) throws Exception {
        Path emptyBackupDir = tempDir.resolve("empty-backup");
        Path targetDir = tempDir.resolve("target");
        Files.createDirectories(emptyBackupDir);

        ProcessBuilder restorePb = new ProcessBuilder("bash", "infrastructure/restore.sh");
        restorePb.environment().put("TARGET_DATA_DIR", targetDir.toAbsolutePath().toString());
        restorePb.environment().put("SECURE_STORAGE_DIR", emptyBackupDir.toAbsolutePath().toString());

        Process restoreProcess = restorePb.start();
        String errorOutput;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(restoreProcess.getErrorStream()))) {
            errorOutput = reader.lines().collect(Collectors.joining("\n"));
        }
        int exitCode = restoreProcess.waitFor();

        assertThat(exitCode).isNotEqualTo(0);
        assertThat(errorOutput).contains("No valid backup file found to restore");
    }
}
