package com.eneik.generated.config;

import com.eneik.generated.Application;
import com.eneik.generated.security.SessionInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
class WebConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebConfig webConfig;

    @Autowired
    private SessionInterceptor sessionInterceptor;

    @Test
    void testWebConfigBeanInitialization() {
        assertThat(webConfig).isNotNull();
        assertThat(sessionInterceptor).isNotNull();
    }

    @Test
    void testSessionInterceptorAppliesToMaterialsManagementRoute() throws Exception {
        // Management route POST /api/v1/materials is intercepted and rejected with 401 when unauthenticated
        mockMvc.perform(post("/api/v1/materials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message").value("Missing or invalid session context. Authentication token is required for management functions."));
    }

    @Test
    void testSessionInterceptorAppliesToNestedMaterialsRoute() throws Exception {
        // Management route POST /api/v1/materials/1/publish is intercepted and rejected with 401 when unauthenticated
        mockMvc.perform(post("/api/v1/materials/1/publish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void testNonProtectedMethodOnMaterialsRouteAllowedWithoutAuth() throws Exception {
        // GET /api/v1/materials/search is on /api/v1/materials/** but preHandle returns true for non-management methods
        mockMvc.perform(get("/api/v1/materials/search"))
                .andExpect(status().isOk());
    }
}
