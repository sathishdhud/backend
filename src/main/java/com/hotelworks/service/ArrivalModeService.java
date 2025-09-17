package com.hotelworks.service;

import com.hotelworks.dto.request.ArrivalModeRequest;
import com.hotelworks.dto.response.ArrivalModeResponse;
import com.hotelworks.entity.ArrivalMode;
import com.hotelworks.repository.ArrivalModeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ArrivalModeService {
    
    @Autowired
    private ArrivalModeRepository arrivalModeRepository;
    
    @Autowired
    private NumberGenerationService numberGenerationService;
    
    /**
     * Create a new arrival mode
     */
    public ArrivalModeResponse createArrivalMode(ArrivalModeRequest request) {
        // Generate arrival mode ID if not provided
        String id = request.getId();
        if (id == null || id.trim().isEmpty()) {
            id = numberGenerationService.generateArrivalModeId();
        }
        
        // Check if arrival mode already exists
        if (arrivalModeRepository.existsById(id)) {
            throw new RuntimeException("Arrival mode already exists: " + id);
        }
        
        ArrivalMode arrivalMode = new ArrivalMode();
        arrivalMode.setId(id);
        arrivalMode.setArrivalMode(request.getArrivalMode());
        
        ArrivalMode savedArrivalMode = arrivalModeRepository.save(arrivalMode);
        return mapToArrivalModeResponse(savedArrivalMode);
    }
    
    /**
     * Get all arrival modes
     */
    public List<ArrivalModeResponse> getAllArrivalModes() {
        List<ArrivalMode> arrivalModes = arrivalModeRepository.findAll();
        return arrivalModes.stream()
            .map(this::mapToArrivalModeResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get arrival mode by ID
     */
    public ArrivalModeResponse getArrivalMode(String id) {
        ArrivalMode arrivalMode = arrivalModeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Arrival mode not found: " + id));
        return mapToArrivalModeResponse(arrivalMode);
    }
    
    /**
     * Update arrival mode
     */
    public ArrivalModeResponse updateArrivalMode(String id, ArrivalModeRequest request) {
        ArrivalMode arrivalMode = arrivalModeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Arrival mode not found: " + id));
        
        arrivalMode.setArrivalMode(request.getArrivalMode());
        
        ArrivalMode updatedArrivalMode = arrivalModeRepository.save(arrivalMode);
        return mapToArrivalModeResponse(updatedArrivalMode);
    }
    
    /**
     * Delete arrival mode
     */
    public void deleteArrivalMode(String id) {
        if (!arrivalModeRepository.existsById(id)) {
            throw new RuntimeException("Arrival mode not found: " + id);
        }
        arrivalModeRepository.deleteById(id);
    }
    
    private ArrivalModeResponse mapToArrivalModeResponse(ArrivalMode arrivalMode) {
        ArrivalModeResponse response = new ArrivalModeResponse();
        response.setId(arrivalMode.getId());
        response.setArrivalMode(arrivalMode.getArrivalMode());
        return response;
    }
}