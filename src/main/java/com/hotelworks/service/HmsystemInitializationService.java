package com.hotelworks.service;

import com.hotelworks.entity.Hmsystem;
import com.hotelworks.repository.HmsystemRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class HmsystemInitializationService {
    
    @Autowired
    private HmsystemRepository hmsystemRepository;
    
    @PostConstruct
    public void initializeHmsystem() {
        // Check if HMS system record exists
        Optional<Hmsystem> latestRecord = hmsystemRepository.findLatestRecord();
        
        if (!latestRecord.isPresent()) {
            // Create initial HMS system record
            Hmsystem initialRecord = new Hmsystem();
            initialRecord.setShiftDate(LocalDate.now());
            initialRecord.setRunningShift(1);
            initialRecord.setTotalShift(3); // Default to 3 shifts
            hmsystemRepository.save(initialRecord);
            
            System.out.println("Initialized HMS system with default values: " +
                "Date=" + initialRecord.getShiftDate() + 
                ", Running Shift=" + initialRecord.getRunningShift() + 
                ", Total Shifts=" + initialRecord.getTotalShift());
        }
    }
}