package com.bogecom.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bogecom.auth.dto.LoginRequest;
import com.bogecom.auth.service.AuthService;
import com.bogecom.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(
    addFilters = false) // Disable security filters for unit test of controller layer
class AuthControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private AuthService authService;

  @MockBean private JwtTokenProvider jwtTokenProvider;

  @Test
  void login_Success_ReturnsTokensInCookies() throws Exception {
    LoginRequest request = new LoginRequest("test@test.com", "Password123!");
    String[] mockTokens = {"access123", "refresh123"};

    when(authService.login(any(), any())).thenReturn(mockTokens);

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(cookie().value("accessToken", "access123"))
        .andExpect(cookie().value("refreshToken", "refresh123"))
        .andExpect(cookie().httpOnly("accessToken", true))
        .andExpect(cookie().secure("accessToken", false)); // Running test profile defaults to false
  }

  @Test
  void login_InvalidRequest_ReturnsBadRequest() throws Exception {
    LoginRequest request = new LoginRequest("invalidemail", ""); // Violates @Email and @NotBlank

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.errors.email").exists())
        .andExpect(jsonPath("$.errors.password").exists());
  }
}
