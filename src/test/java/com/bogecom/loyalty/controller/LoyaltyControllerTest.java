package com.bogecom.loyalty.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bogecom.loyalty.dto.AddCoinsRequest;
import com.bogecom.loyalty.dto.CoinWalletDto;
import com.bogecom.loyalty.entity.CoinTransactionType;
import com.bogecom.loyalty.service.LoyaltyService;
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

@WebMvcTest(LoyaltyController.class)
@AutoConfigureMockMvc(addFilters = false)
class LoyaltyControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private LoyaltyService loyaltyService;

  @MockBean private JwtTokenProvider jwtTokenProvider;

  @Test
  void getWallet_AsUser_ReturnsWallet() throws Exception {
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(
            1L, null, List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));

    CoinWalletDto response = new CoinWalletDto(1L, 1L, 100L, new ArrayList<>());

    when(loyaltyService.getWallet(nullable(Long.class))).thenReturn(response);

    mockMvc
        .perform(get("/api/v1/loyalty/wallet").with(authentication(auth)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.balance").value(100));
  }

  @Test
  void addCoins_AsAdmin_ReturnsUpdatedWallet() throws Exception {
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(
            1L, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

    AddCoinsRequest request =
        new AddCoinsRequest(50L, CoinTransactionType.MANUAL_ADJUSTMENT, null, "Bonus");

    CoinWalletDto response = new CoinWalletDto(1L, 1L, 150L, new ArrayList<>());

    when(loyaltyService.addCoins(nullable(Long.class), any(AddCoinsRequest.class)))
        .thenReturn(response);

    mockMvc
        .perform(
            post("/api/v1/loyalty/wallet/add")
                .with(authentication(auth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.balance").value(150));
  }
}
