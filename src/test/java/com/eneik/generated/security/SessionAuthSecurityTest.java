package com.eneik.generated.security;

import com.eneik.generated.dto.LoginRequest;
import com.eneik.generated.dto.LoginResponse;
import com.eneik.generated.service.SessionAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SessionAuthSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SessionAuthService sessionAuthService;

    @BeforeEach
    void setUp() {
        sessionAuthService.clearAllSessions();
    }

    @Test
    void givenValidCredentials_whenLogin_thenReturnsTokenAndEstablishesSession() throws Exception {
        LoginRequest loginRequest = new LoginRequest("epidemiologist@example.com", "SecurePassword2026!");

        String responseBody = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("epidemiologist@example.com"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andReturn().getResponse().getContentAsString();

        LoginResponse response = objectMapper.readValue(responseBody, LoginResponse.class);
        assertThat(sessionAuthService.validateSession(response.getToken())).isPresent();
    }

    @Test
    void givenInvalidCredentials_whenLogin_thenReturns401() throws Exception {
        LoginRequest loginRequest = new LoginRequest("baduser@example.com", "");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_PAYLOAD"));

        LoginRequest invalidPass = new LoginRequest("baduser@example.com", "short");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPass)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }

    @Test
    void givenUnauthenticatedRequest_whenAccessingManagementFunction_thenRejectedWith401() throws Exception {
        mockMvc.perform(post("/api/v1/materials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message").value("Missing or invalid session context. Authentication token is required for management functions."));
    }

    @Test
    void givenValidSessionToken_whenAccessingManagementFunction_thenAllowed() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin@example.com", "AdminPass123!");
        LoginResponse loginResponse = sessionAuthService.authenticate(loginRequest).orElseThrow();

        mockMvc.perform(post("/api/v1/materials")
                        .header("Authorization", "Bearer " + loginResponse.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Outbreak Protocol\",\"pathogenType\":\"VIRUS\",\"content\":\"Detailed procedures...\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Outbreak Protocol"));
    }

    @Test
    void givenPublicEndpoint_whenUnauthenticated_thenAllowed() throws Exception {
        mockMvc.perform(get("/api/v1/materials/search"))
                .andExpect(status().isOk());
    }
}
