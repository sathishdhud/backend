package com.hotelworks.service;

import com.hotelworks.dto.request.SalesRequest;
import com.hotelworks.dto.response.SalesResponse;
import com.hotelworks.entity.Sales;
import com.hotelworks.repository.SalesRepository;
import com.hotelworks.service.NumberGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SalesService {
    
    @Autowired
    private SalesRepository salesRepository;
    
    @Autowired
    private NumberGenerationService numberGenerationService;
    
    public SalesResponse createSales(SalesRequest request) {
        Sales sales = new Sales();
        sales.setSalesId(numberGenerationService.generateSalesId());
        sales.setReceiptNumber(request.getReceiptNumber());
        sales.setDate(request.getDate());
        sales.setModeOfPayment(request.getModeOfPayment());
        sales.setAmount(request.getAmount());
        sales.setVoucherNumber(request.getVoucherNumber());
        sales.setNarration(request.getNarration());
        
        Sales savedSales = salesRepository.save(sales);
        return mapToResponse(savedSales);
    }
    
    public SalesResponse getSalesById(String salesId) {
        Sales sales = salesRepository.findById(salesId)
            .orElseThrow(() -> new RuntimeException("Sales not found with ID: " + salesId));
        return mapToResponse(sales);
    }
    
    public List<SalesResponse> getAllSales() {
        return salesRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public SalesResponse updateSales(String salesId, SalesRequest request) {
        Sales sales = salesRepository.findById(salesId)
            .orElseThrow(() -> new RuntimeException("Sales not found with ID: " + salesId));
        
        sales.setReceiptNumber(request.getReceiptNumber());
        sales.setDate(request.getDate());
        sales.setModeOfPayment(request.getModeOfPayment());
        sales.setAmount(request.getAmount());
        sales.setVoucherNumber(request.getVoucherNumber());
        sales.setNarration(request.getNarration());
        
        Sales updatedSales = salesRepository.save(sales);
        return mapToResponse(updatedSales);
    }
    
    public void deleteSales(String salesId) {
        if (!salesRepository.existsById(salesId)) {
            throw new RuntimeException("Sales not found with ID: " + salesId);
        }
        salesRepository.deleteById(salesId);
    }
    
    private SalesResponse mapToResponse(Sales sales) {
        return new SalesResponse(
            sales.getSalesId(),
            sales.getReceiptNumber(),
            sales.getDate(),
            sales.getModeOfPayment(),
            sales.getAmount(),
            sales.getVoucherNumber(),
            sales.getNarration()
        );
    }
}