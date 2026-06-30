package com.bogecom.shipping.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bogecom.security.JwtTokenProvider;
import com.bogecom.shipping.dto.CreateShippingRequest;
import com.bogecom.shipping.dto.ShippingDto;
import com.bogecom.shipping.entity.ShippingStatus;
import com.bogecom.shipping.service.ShippingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
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

@WebMvcTest(ShippingController.class)
@AutoConfigureMockMvc(addFilters = false)
class ShippingControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private ShippingService shippingService;

  @MockBean private JwtTokenProvider jwtTokenProvider;

  @Test
  void createShipping_AsAdmin_ReturnsCreatedShipping() throws Exception {
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(
            1L, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

    CreateShippingRequest request = new CreateShippingRequest(10L, 1L, new BigDecimal("15.50"));

    ShippingDto response =
        new ShippingDto(
            1L,
            10L,
            1L,
            ShippingStatus.PENDING,
            null,
            null,
            new BigDecimal("15.50"),
            null,
            LocalDateTime.now());

    when(shippingService.createShipping(any(CreateShippingRequest.class))).thenReturn(response);

    mockMvc
        .perform(
            post("/api/v1/shipping")
                .with(authentication(auth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value(1L));
  }

  @Test
  void getShippingByOrderId_AsUser_ReturnsShipping() throws Exception {
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(
            1L, null, List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));

    ShippingDto response =
        new ShippingDto(
            1L,
            10L,
            1L,
            ShippingStatus.PENDING,
            null,
            null,
            new BigDecimal("15.50"),
            null,
            LocalDateTime.now());

    when(shippingService.getShippingByOrderId(nullable(Long.class))).thenReturn(response);

    mockMvc
        .perform(get("/api/v1/shipping/orders/10").with(authentication(auth)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value(1L));
  }
}
