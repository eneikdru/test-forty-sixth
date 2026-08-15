package com.eneik.generated.controller;

import com.eneik.generated.Application;
import com.eneik.generated.dto.LoginRequest;
import com.eneik.generated.service.SessionAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
class AuthControllerTest {

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
    void testLoginSuccess() throws Exception {
        LoginRequest validRequest = new LoginRequest("user@example.com", "ValidPass123!");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.username").value("user@example.com"))
                .andExpect(jsonPath("$.expiresAt", notNullValue()));
    }

    @Test
    void testLoginInvalidCredentials() throws Exception {
        LoginRequest invalidRequest = new LoginRequest("user@example.com", "123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message").value("Invalid username or password."))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.details[0]").value("Authentication failed for user: user@example.com"));
    }

    @Test
    void testLoginValidationFailureBlankUsername() throws Exception {
        LoginRequest invalidRequest = new LoginRequest("", "ValidPass123!");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("INVALID_PAYLOAD"))
                .andExpect(jsonPath("$.message").value("Validation failed for login request."))
                .andExpect(jsonPath("$.details[0]", containsString("Username or email is required")));
    }

    @Test
    void testLoginValidationFailureBlankPassword() throws Exception {
        LoginRequest invalidRequest = new LoginRequest("user@example.com", "   ");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("INVALID_PAYLOAD"))
                .andExpect(jsonPath("$.message").value("Validation failed for login request."))
                .andExpect(jsonPath("$.details[0]", containsString("Password is required")));
    }
}
