package com.hotelworks.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelworks.dto.request.SalesRequest;
import com.hotelworks.entity.Sales;
import com.hotelworks.repository.SalesRepository;
import com.hotelworks.service.NumberGenerationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
public class SalesControllerTest {
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    private MockMvc mockMvc;
    
    @MockBean
    private SalesRepository salesRepository;
    
    @MockBean
    private NumberGenerationService numberGenerationService;
    
    private Sales sampleSales;
    
    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        sampleSales = new Sales();
        sampleSales.setSalesId("SALES-001");
        sampleSales.setReceiptNumber("REC-001");
        sampleSales.setDate(LocalDate.now());
        sampleSales.setModeOfPayment("Cash");
        sampleSales.setAmount(new BigDecimal("1000.00"));
        sampleSales.setVoucherNumber("V-001");
        sampleSales.setNarration("Room service");
    }
    
    @Test
    public void testCreateSales() throws Exception {
        when(numberGenerationService.generateSalesId()).thenReturn("SALES-001");
        when(salesRepository.save(any(Sales.class))).thenReturn(sampleSales);
        
        SalesRequest request = new SalesRequest();
        request.setReceiptNumber("REC-001");
        request.setDate(LocalDate.now());
        request.setModeOfPayment("Cash");
        request.setAmount(new BigDecimal("1000.00"));
        request.setVoucherNumber("V-001");
        request.setNarration("Room service");
        
        mockMvc.perform(post("/api/sales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.salesId").value("SALES-001"))
                .andExpect(jsonPath("$.data.receiptNumber").value("REC-001"));
    }
    
    @Test
    public void testGetSalesById() throws Exception {
        when(salesRepository.findById("SALES-001")).thenReturn(Optional.of(sampleSales));
        
        mockMvc.perform(get("/api/sales/SALES-001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.salesId").value("SALES-001"))
                .andExpect(jsonPath("$.data.receiptNumber").value("REC-001"));
    }
    
    @Test
    public void testUpdateSales() throws Exception {
        when(salesRepository.findById("SALES-001")).thenReturn(Optional.of(sampleSales));
        when(salesRepository.save(any(Sales.class))).thenReturn(sampleSales);
        
        SalesRequest request = new SalesRequest();
        request.setReceiptNumber("REC-001");
        request.setDate(LocalDate.now());
        request.setModeOfPayment("Credit Card");
        request.setAmount(new BigDecimal("1500.00"));
        request.setVoucherNumber("V-001");
        request.setNarration("Room service updated");
        
        mockMvc.perform(put("/api/sales/SALES-001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.modeOfPayment").value("Credit Card"))
                .andExpect(jsonPath("$.data.amount").value(1500.00));
    }
    
    @Test
    public void testDeleteSales() throws Exception {
        when(salesRepository.existsById("SALES-001")).thenReturn(true);
        
        mockMvc.perform(delete("/api/sales/SALES-001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Sales record deleted successfully"));
    }
    
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}