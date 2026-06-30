package com.bogecom.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bogecom.security.JwtTokenProvider;
import com.bogecom.user.dto.AddressDto;
import com.bogecom.user.dto.UpdateProfileRequest;
import com.bogecom.user.dto.UserResponse;
import com.bogecom.user.entity.AccountStatus;
import com.bogecom.user.entity.Role;
import com.bogecom.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable actual filters for pure controller layer tests
class UserControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private UserService userService;

  @MockBean private JwtTokenProvider jwtTokenProvider;

  @Test
  void updateProfile_ReturnsUpdatedUser() throws Exception {
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(
            1L, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    UpdateProfileRequest request = new UpdateProfileRequest("John", "Doe", "1234567890");
    UserResponse response =
        new UserResponse(
            1L,
            "john@test.com",
            "John",
            "Doe",
            "1234567890",
            Role.CUSTOMER,
            AccountStatus.ACTIVE,
            LocalDateTime.now(),
            null);

    when(userService.updateProfile(any(), any(UpdateProfileRequest.class))).thenReturn(response);

    mockMvc
        .perform(
            put("/api/v1/users/me")
                .with(authentication(auth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.firstName").value("John"));
  }

  @Test
  void addAddress_ReturnsNewAddress() throws Exception {
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(
            1L, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    AddressDto request =
        new AddressDto(null, "123 Main St", "", "City", "State", "12345", "Country", true);
    AddressDto response =
        new AddressDto(1L, "123 Main St", "", "City", "State", "12345", "Country", true);

    when(userService.addAddress(any(), any(AddressDto.class))).thenReturn(response);

    mockMvc
        .perform(
            post("/api/v1/users/me/addresses")
                .with(authentication(auth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value(1L))
        .andExpect(jsonPath("$.data.streetLine1").value("123 Main St"));
  }

  @Test
  void getAllUsers_ReturnsList() throws Exception {
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(
            1L, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    UserResponse response =
        new UserResponse(
            1L,
            "john@test.com",
            "John",
            "Doe",
            "1234567890",
            Role.CUSTOMER,
            AccountStatus.ACTIVE,
            LocalDateTime.now(),
            null);

    when(userService.getAllUsers()).thenReturn(List.of(response));

    mockMvc
        .perform(get("/api/v1/users").with(authentication(auth)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data[0].id").value(1L));
  }
}
