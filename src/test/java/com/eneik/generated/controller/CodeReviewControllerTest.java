package com.eneik.generated.controller;

import com.eneik.generated.Application;
import com.eneik.generated.dto.CodeReviewRequest;
import com.eneik.generated.dto.CodeReviewVerdictDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
class CodeReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testProcessReview_Success() throws Exception {
        CodeReviewVerdictDto verdict = new CodeReviewVerdictDto(
                0,
                "approve",
                "PR #47 code verified with comprehensive unit and integration tests.",
                List.of()
        );
        CodeReviewRequest request = new CodeReviewRequest(47, List.of(verdict));

        mockMvc.perform(post("/api/v1/code-reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reviewId", notNullValue()))
                .andExpect(jsonPath("$.prNumber").value(47))
                .andExpect(jsonPath("$.status").value("RECORDED"))
                .andExpect(jsonPath("$.createdAt", notNullValue()))
                .andExpect(jsonPath("$.verdicts[0].verdict").value("approve"));
    }

    @Test
    void testProcessReview_RejectedWhenApprovalCriticalReasonEmpty() throws Exception {
        CodeReviewVerdictDto verdict = new CodeReviewVerdictDto(
                0,
                "approve",
                "",
                List.of()
        );
        CodeReviewRequest request = new CodeReviewRequest(47, List.of(verdict));

        mockMvc.perform(post("/api/v1/code-reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("INVALID_REVIEW"))
                .andExpect(jsonPath("$.message").value("Approval review requires a substantive criticalReason"))
                .andExpect(jsonPath("$.details[0]").value("Approval review requires a substantive criticalReason"));
    }

    @Test
    void testProcessReview_RejectedWhenPrNumberNull() throws Exception {
        CodeReviewVerdictDto verdict = new CodeReviewVerdictDto(
                0,
                "approve",
                "Substantive critique",
                List.of()
        );
        CodeReviewRequest request = new CodeReviewRequest(null, List.of(verdict));

        mockMvc.perform(post("/api/v1/code-reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("INVALID_PAYLOAD"));
    }
}
