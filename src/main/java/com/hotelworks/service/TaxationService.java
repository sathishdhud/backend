package com.hotelworks.service;

import com.hotelworks.dto.request.TaxationRequest;
import com.hotelworks.dto.response.TaxationResponse;
import com.hotelworks.entity.Taxation;
import com.hotelworks.repository.TaxationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaxationService {
    
    @Autowired
    private TaxationRepository taxationRepository;
    
    @Autowired
    private NumberGenerationService numberGenerationService;
    
    /**
     * Create a new tax
     */
    public TaxationResponse createTax(TaxationRequest request) {
        // Generate tax ID if not provided
        String taxId = request.getTaxId();
        if (taxId == null || taxId.trim().isEmpty()) {
            taxId = numberGenerationService.generateTaxId();
        }
        
        // Check if tax already exists
        if (taxationRepository.existsById(taxId)) {
            throw new RuntimeException("Tax already exists: " + taxId);
        }
        
        Taxation tax = new Taxation();
        tax.setTaxId(taxId);
        tax.setTaxName(request.getTaxName());
        tax.setPercentage(request.getPercentage());
        
        Taxation savedTax = taxationRepository.save(tax);
        return mapToTaxationResponse(savedTax);
    }
    
    /**
     * Get all taxes
     */
    public List<TaxationResponse> getAllTaxes() {
        List<Taxation> taxes = taxationRepository.findAll();
        return taxes.stream()
            .map(this::mapToTaxationResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get tax by ID
     */
    public TaxationResponse getTax(String taxId) {
        Taxation tax = taxationRepository.findById(taxId)
            .orElseThrow(() -> new RuntimeException("Tax not found: " + taxId));
        return mapToTaxationResponse(tax);
    }
    
    /**
     * Update tax
     */
    public TaxationResponse updateTax(String taxId, TaxationRequest request) {
        Taxation tax = taxationRepository.findById(taxId)
            .orElseThrow(() -> new RuntimeException("Tax not found: " + taxId));
        
        tax.setTaxName(request.getTaxName());
        tax.setPercentage(request.getPercentage());
        
        Taxation updatedTax = taxationRepository.save(tax);
        return mapToTaxationResponse(updatedTax);
    }
    
    /**
     * Delete tax
     */
    public void deleteTax(String taxId) {
        if (!taxationRepository.existsById(taxId)) {
            throw new RuntimeException("Tax not found: " + taxId);
        }
        taxationRepository.deleteById(taxId);
    }
    
    private TaxationResponse mapToTaxationResponse(Taxation tax) {
        TaxationResponse response = new TaxationResponse();
        response.setTaxId(tax.getTaxId());
        response.setTaxName(tax.getTaxName());
        response.setPercentage(tax.getPercentage());
        return response;
    }
}