package com.bogecom.cart.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bogecom.cart.dto.AddToCartRequest;
import com.bogecom.cart.dto.CartDto;
import com.bogecom.cart.entity.CartStatus;
import com.bogecom.cart.service.CartService;
import com.bogecom.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
class CartControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private CartService cartService;

  @MockBean private JwtTokenProvider jwtTokenProvider;

  @Test
  void getCart_AsUser_ReturnsCart() throws Exception {
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(
            1L, null, List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));

    CartDto response = new CartDto(1L, 1L, null, CartStatus.ACTIVE, new ArrayList<>());

    when(cartService.getCart(nullable(Long.class), nullable(String.class))).thenReturn(response);

    mockMvc
        .perform(get("/api/v1/carts").with(authentication(auth)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value(1L));
  }

  @Test
  void addToCart_AsUser_ReturnsUpdatedCart() throws Exception {
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(
            1L, null, List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));

    AddToCartRequest request = new AddToCartRequest(100L, 2, null);

    CartDto response = new CartDto(1L, 1L, null, CartStatus.ACTIVE, new ArrayList<>());

    when(cartService.addToCart(nullable(Long.class), any(AddToCartRequest.class)))
        .thenReturn(response);

    mockMvc
        .perform(
            post("/api/v1/carts/items")
                .with(authentication(auth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
  }
}
