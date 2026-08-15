package com.eneik.generated;

import com.eneik.generated.dto.PasswordResetConfirmRequest;
import com.eneik.generated.dto.PasswordResetRequest;
import com.eneik.generated.entity.RecoveryToken;
import com.eneik.generated.repository.RecoveryTokenRepository;
import com.eneik.generated.service.LoggingEmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RecoveryAuthTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RecoveryTokenRepository repository;

    @Autowired
    private LoggingEmailService emailService;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        emailService.clear();
    }

    @Test
    void givenValidEmail_whenRequestingReset_thenTokenIsGeneratedAndEmailed() throws Exception {
        PasswordResetRequest request = new PasswordResetRequest("epidemiologist@example.com");

        mockMvc.perform(post("/api/v1/auth/recovery/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.expiresInMinutes").value(30));

        assertThat(repository.count()).isEqualTo(1);
        RecoveryToken token = repository.findAll().get(0);
        assertThat(token.getEmail()).isEqualTo("epidemiologist@example.com");
        assertThat(token.getStatus()).isEqualTo(RecoveryToken.Status.PENDING);

        assertThat(emailService.getSentEmails()).hasSize(1);
        assertThat(emailService.getSentEmails().get(0)).contains("epidemiologist@example.com:" + token.getToken());
    }

    @Test
    void givenExpiredToken_whenAttemptingToReset_thenRejectedWith401() throws Exception {
        Instant fixedNow = Instant.parse("2026-08-15T00:00:00Z");
        Instant expiredAt = fixedNow.minus(Duration.ofMinutes(10));
        RecoveryToken expiredToken = new RecoveryToken("doctor@example.com", "tok_expired_123", RecoveryToken.Status.PENDING, expiredAt, expiredAt.minus(Duration.ofMinutes(30)));
        repository.save(expiredToken);

        PasswordResetConfirmRequest confirmRequest = new PasswordResetConfirmRequest("tok_expired_123", "NewSecurePass2026!");

        mockMvc.perform(post("/api/v1/auth/recovery/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(confirmRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_TOKEN"))
                .andExpect(jsonPath("$.message").value("Recovery token is invalid, expired, or already consumed."));
    }

    @Test
    void givenValidToken_whenConfirmReset_thenSuccessAndCannotBeReused() throws Exception {
        Instant fixedNow = Instant.now();
        Instant expiresAt = fixedNow.plus(Duration.ofMinutes(30));
        RecoveryToken validToken = new RecoveryToken("researcher@example.com", "tok_valid_456", RecoveryToken.Status.PENDING, expiresAt, fixedNow);
        repository.save(validToken);

        PasswordResetConfirmRequest confirmRequest = new PasswordResetConfirmRequest("tok_valid_456", "NewSecurePass2026!");

        mockMvc.perform(post("/api/v1/auth/recovery/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(confirmRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        RecoveryToken updatedToken = repository.findByToken("tok_valid_456").orElseThrow();
        assertThat(updatedToken.getStatus()).isEqualTo(RecoveryToken.Status.USED);

        // Second attempt with same token should fail with 401
        mockMvc.perform(post("/api/v1/auth/recovery/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(confirmRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_TOKEN"));
    }

    @Test
    void givenTokenValidation_whenTokenIsValid_then200AndMaskedEmail() throws Exception {
        Instant fixedNow = Instant.now();
        Instant expiresAt = fixedNow.plus(Duration.ofMinutes(30));
        RecoveryToken token = new RecoveryToken("john.doe@example.com", "tok_val_789", RecoveryToken.Status.PENDING, expiresAt, fixedNow);
        repository.save(token);

        mockMvc.perform(get("/api/v1/auth/recovery/validate-token")
                        .param("token", "tok_val_789"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.maskedEmail").value("j***e@example.com"));
    }

    @Test
    void givenTokenValidation_whenTokenIsExpiredOrNotFound_then404() throws Exception {
        mockMvc.perform(get("/api/v1/auth/recovery/validate-token")
                        .param("token", "non_existent_token"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TOKEN_EXPIRED_OR_INVALID"));
    }
}
