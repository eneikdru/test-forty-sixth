package com.eneik.generated.review;

import com.eneik.generated.Application;
import com.eneik.generated.review.dto.CodeReviewRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
class CodeReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/reviews with valid approval review returns 200 OK")
    void testValidApprovalReview() throws Exception {
        CodeReviewRequest request = new CodeReviewRequest(
                "PR-200",
                "approve",
                "Substantive reasoning explaining that architecture and coding standards are satisfied.",
                List.of()
        );

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prId").value("PR-200"))
                .andExpect(jsonPath("$.verdict").value("approve"))
                .andExpect(jsonPath("$.status").value("RECORDED"))
                .andExpect(jsonPath("$.reviewId").exists());
    }

    @Test
    @DisplayName("POST /api/reviews with empty approval review returns 400 Bad Request")
    void testEmptyApprovalReviewRejected() throws Exception {
        CodeReviewRequest request = new CodeReviewRequest(
                "PR-201",
                "approve",
                "",
                List.of()
        );

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REVIEW"))
                .andExpect(jsonPath("$.message").value("Approval code reviews require a substantive criticalReason. Empty or faked reviews are rejected."));
    }
}
