package com.hotelworks.service;

import com.hotelworks.dto.request.ResvSourceRequest;
import com.hotelworks.dto.response.ResvSourceResponse;
import com.hotelworks.entity.ResvSource;
import com.hotelworks.repository.ResvSourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ResvSourceService {
    
    @Autowired
    private ResvSourceRepository resvSourceRepository;
    
    @Autowired
    private NumberGenerationService numberGenerationService;
    
    /**
     * Create a new reservation source
     */
    public ResvSourceResponse createResvSource(ResvSourceRequest request) {
        // Generate reservation source ID if not provided
        String id = request.getId();
        if (id == null || id.trim().isEmpty()) {
            id = numberGenerationService.generateResvSourceId();
        }
        
        // Check if reservation source already exists
        if (resvSourceRepository.existsById(id)) {
            throw new RuntimeException("Reservation source already exists: " + id);
        }
        
        ResvSource resvSource = new ResvSource();
        resvSource.setId(id);
        resvSource.setResvSource(request.getResvSource());
        
        ResvSource savedResvSource = resvSourceRepository.save(resvSource);
        return mapToResvSourceResponse(savedResvSource);
    }
    
    /**
     * Get all reservation sources
     */
    public List<ResvSourceResponse> getAllResvSources() {
        List<ResvSource> resvSources = resvSourceRepository.findAll();
        return resvSources.stream()
            .map(this::mapToResvSourceResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get reservation source by ID
     */
    public ResvSourceResponse getResvSource(String id) {
        ResvSource resvSource = resvSourceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Reservation source not found: " + id));
        return mapToResvSourceResponse(resvSource);
    }
    
    /**
     * Update reservation source
     */
    public ResvSourceResponse updateResvSource(String id, ResvSourceRequest request) {
        ResvSource resvSource = resvSourceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Reservation source not found: " + id));
        
        resvSource.setResvSource(request.getResvSource());
        
        ResvSource updatedResvSource = resvSourceRepository.save(resvSource);
        return mapToResvSourceResponse(updatedResvSource);
    }
    
    /**
     * Delete reservation source
     */
    public void deleteResvSource(String id) {
        if (!resvSourceRepository.existsById(id)) {
            throw new RuntimeException("Reservation source not found: " + id);
        }
        resvSourceRepository.deleteById(id);
    }
    
    private ResvSourceResponse mapToResvSourceResponse(ResvSource resvSource) {
        ResvSourceResponse response = new ResvSourceResponse();
        response.setId(resvSource.getId());
        response.setResvSource(resvSource.getResvSource());
        return response;
    }
}