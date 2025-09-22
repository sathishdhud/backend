package com.hotelworks.service;

import com.hotelworks.entity.ArrivalMode;
import com.hotelworks.repository.ArrivalModeRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataInitializationService {

    @Autowired
    private ArrivalModeRepository arrivalModeRepository;

    @PostConstruct
    public void initializeData() {
        // Initialize default arrival modes if they don't exist
        createArrivalModeIfNotExists("WALKIN", "Walk In");
        createArrivalModeIfNotExists("PHONE", "Phone Booking");
        createArrivalModeIfNotExists("ONLINE", "Online Booking");
        createArrivalModeIfNotExists("AGENT", "Travel Agent");
        createArrivalModeIfNotExists("CORP", "Corporate Booking");
    }

    private void createArrivalModeIfNotExists(String id, String name) {
        if (!arrivalModeRepository.existsById(id)) {
            ArrivalMode arrivalMode = new ArrivalMode();
            arrivalMode.setId(id);
            arrivalMode.setArrivalMode(name);
            arrivalModeRepository.save(arrivalMode);
        }
    }
}