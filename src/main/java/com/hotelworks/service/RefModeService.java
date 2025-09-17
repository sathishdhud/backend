package com.hotelworks.service;

import com.hotelworks.dto.request.RefModeRequest;
import com.hotelworks.dto.response.RefModeResponse;
import com.hotelworks.entity.RefMode;
import com.hotelworks.repository.RefModeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RefModeService {
    
    @Autowired
    private RefModeRepository refModeRepository;
    
    @Autowired
    private NumberGenerationService numberGenerationService;
    
    /**
     * Create a new reference mode
     */
    public RefModeResponse createRefMode(RefModeRequest request) {
        // Generate ref mode ID if not provided
        String id = request.getId();
        if (id == null || id.trim().isEmpty()) {
            id = numberGenerationService.generateRefModeId();
        }
        
        // Check if ref mode already exists
        if (refModeRepository.existsById(id)) {
            throw new RuntimeException("Reference mode already exists: " + id);
        }
        
        RefMode refMode = new RefMode();
        refMode.setId(id);
        refMode.setRefMode(request.getRefMode());
        
        RefMode savedRefMode = refModeRepository.save(refMode);
        return mapToRefModeResponse(savedRefMode);
    }
    
    /**
     * Get all reference modes
     */
    public List<RefModeResponse> getAllRefModes() {
        List<RefMode> refModes = refModeRepository.findAll();
        return refModes.stream()
            .map(this::mapToRefModeResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get reference mode by ID
     */
    public RefModeResponse getRefMode(String id) {
        RefMode refMode = refModeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Reference mode not found: " + id));
        return mapToRefModeResponse(refMode);
    }
    
    /**
     * Update reference mode
     */
    public RefModeResponse updateRefMode(String id, RefModeRequest request) {
        RefMode refMode = refModeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Reference mode not found: " + id));
        
        refMode.setRefMode(request.getRefMode());
        
        RefMode updatedRefMode = refModeRepository.save(refMode);
        return mapToRefModeResponse(updatedRefMode);
    }
    
    /**
     * Delete reference mode
     */
    public void deleteRefMode(String id) {
        if (!refModeRepository.existsById(id)) {
            throw new RuntimeException("Reference mode not found: " + id);
        }
        refModeRepository.deleteById(id);
    }
    
    private RefModeResponse mapToRefModeResponse(RefMode refMode) {
        RefModeResponse response = new RefModeResponse();
        response.setId(refMode.getId());
        response.setRefMode(refMode.getRefMode());
        return response;
    }
}