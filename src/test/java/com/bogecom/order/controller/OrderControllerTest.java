package com.bogecom.order.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bogecom.order.dto.CreateOrderRequest;
import com.bogecom.order.dto.OrderDto;
import com.bogecom.order.entity.OrderStatus;
import com.bogecom.order.service.OrderService;
import com.bogecom.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private OrderService orderService;

  @MockBean private JwtTokenProvider jwtTokenProvider;

  @Test
  void createOrder_AsUser_ReturnsCreatedOrder() throws Exception {
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(
            1L, null, List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));

    CreateOrderRequest request = new CreateOrderRequest(10L, 1L, 1L);

    OrderDto response =
        new OrderDto(
            1L,
            1L,
            OrderStatus.PENDING,
            new BigDecimal("100.00"),
            1L,
            1L,
            LocalDateTime.now(),
            new ArrayList<>());

    when(orderService.createOrder(nullable(Long.class), any(CreateOrderRequest.class)))
        .thenReturn(response);

    mockMvc
        .perform(
            post("/api/v1/orders")
                .with(authentication(auth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value(1L));
  }

  @Test
  void getUserOrders_AsUser_ReturnsOrderList() throws Exception {
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(
            1L, null, List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));

    OrderDto response =
        new OrderDto(
            1L,
            1L,
            OrderStatus.PENDING,
            new BigDecimal("100.00"),
            1L,
            1L,
            LocalDateTime.now(),
            new ArrayList<>());

    when(orderService.getUserOrders(nullable(Long.class))).thenReturn(List.of(response));

    mockMvc
        .perform(get("/api/v1/orders").with(authentication(auth)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data[0].id").value(1L));
  }
}
