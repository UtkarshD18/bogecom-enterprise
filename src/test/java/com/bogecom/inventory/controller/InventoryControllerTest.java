package com.bogecom.inventory.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bogecom.inventory.dto.AdjustInventoryRequest;
import com.bogecom.inventory.dto.InventoryDto;
import com.bogecom.inventory.service.InventoryService;
import com.bogecom.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@WebMvcTest(InventoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InventoryService inventoryService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void getInventoryForProduct_AsAdmin_ReturnsList() throws Exception {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(1L, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        
        InventoryDto response = new InventoryDto(1L, 100L, "WAREHOUSE-A", 50, 5, 55);

        when(inventoryService.getInventoryForProduct(eq(100L))).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/inventory/products/100")
                .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].location").value("WAREHOUSE-A"));
    }

    @Test
    void adjustInventory_AsAdmin_ReturnsAdjustedInventory() throws Exception {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(1L, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        
        AdjustInventoryRequest request = new AdjustInventoryRequest(100L, "WAREHOUSE-B", 20);
                
        InventoryDto response = new InventoryDto(2L, 100L, "WAREHOUSE-B", 20, 0, 20);

        when(inventoryService.adjustInventory(any(AdjustInventoryRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/inventory/adjust")
                .with(authentication(auth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.availableQuantity").value(20));
    }
}
