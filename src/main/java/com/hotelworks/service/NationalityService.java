package com.hotelworks.service;

import com.hotelworks.dto.request.NationalityRequest;
import com.hotelworks.dto.response.NationalityResponse;
import com.hotelworks.entity.Nationality;
import com.hotelworks.repository.NationalityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NationalityService {
    
    @Autowired
    private NationalityRepository nationalityRepository;
    
    @Autowired
    private NumberGenerationService numberGenerationService;
    
    /**
     * Create a new nationality
     */
    public NationalityResponse createNationality(NationalityRequest request) {
        // Generate nationality ID if not provided
        String id = request.getId();
        if (id == null || id.trim().isEmpty()) {
            id = numberGenerationService.generateNationalityId();
        }
        
        // Check if nationality already exists
        if (nationalityRepository.existsById(id)) {
            throw new RuntimeException("Nationality already exists: " + id);
        }
        
        Nationality nationality = new Nationality();
        nationality.setId(id);
        nationality.setNationality(request.getNationality());
        
        Nationality savedNationality = nationalityRepository.save(nationality);
        return mapToNationalityResponse(savedNationality);
    }
    
    /**
     * Get all nationalities
     */
    public List<NationalityResponse> getAllNationalities() {
        List<Nationality> nationalities = nationalityRepository.findAll();
        return nationalities.stream()
            .map(this::mapToNationalityResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get nationality by ID
     */
    public NationalityResponse getNationality(String id) {
        Nationality nationality = nationalityRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Nationality not found: " + id));
        return mapToNationalityResponse(nationality);
    }
    
    /**
     * Update nationality
     */
    public NationalityResponse updateNationality(String id, NationalityRequest request) {
        Nationality nationality = nationalityRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Nationality not found: " + id));
        
        nationality.setNationality(request.getNationality());
        
        Nationality updatedNationality = nationalityRepository.save(nationality);
        return mapToNationalityResponse(updatedNationality);
    }
    
    /**
     * Delete nationality
     */
    public void deleteNationality(String id) {
        if (!nationalityRepository.existsById(id)) {
            throw new RuntimeException("Nationality not found: " + id);
        }
        nationalityRepository.deleteById(id);
    }
    
    private NationalityResponse mapToNationalityResponse(Nationality nationality) {
        NationalityResponse response = new NationalityResponse();
        response.setId(nationality.getId());
        response.setNationality(nationality.getNationality());
        return response;
    }
}