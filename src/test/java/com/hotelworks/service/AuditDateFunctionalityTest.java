package com.hotelworks.service;

import com.hotelworks.entity.Hmsystem;
import com.hotelworks.repository.HmsystemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
public class AuditDateFunctionalityTest {

    @MockBean
    private HmsystemRepository hmsystemRepository;

    @Test
    public void testGetCurrentAuditDateFromHmsystem() {
        // Arrange
        LocalDate expectedAuditDate = LocalDate.of(2025, 11, 7);
        Hmsystem hmsystem = new Hmsystem();
        hmsystem.setShiftDate(expectedAuditDate);
        
        when(hmsystemRepository.findLatestRecord()).thenReturn(Optional.of(hmsystem));
        
        // Create an instance of a service that uses the audit date functionality
        OperationsService operationsService = new OperationsService();
        
        // Use reflection to access the private method
        try {
            java.lang.reflect.Field hmsystemRepoField = OperationsService.class.getDeclaredField("hmsystemRepository");
            hmsystemRepoField.setAccessible(true);
            hmsystemRepoField.set(operationsService, hmsystemRepository);
            
            // Invoke the private method using reflection
            java.lang.reflect.Method method = OperationsService.class.getDeclaredMethod("getCurrentAuditDate");
            method.setAccessible(true);
            LocalDate actualAuditDate = (LocalDate) method.invoke(operationsService);
            
            // Assert
            assertEquals(expectedAuditDate, actualAuditDate);
            verify(hmsystemRepository, times(1)).findLatestRecord();
        } catch (Exception e) {
            fail("Reflection failed: " + e.getMessage());
        }
    }
    
    @Test
    public void testGetCurrentAuditDateFallbackToSystemDate() {
        // Arrange
        when(hmsystemRepository.findLatestRecord()).thenReturn(Optional.empty());
        
        // Create an instance of a service that uses the audit date functionality
        OperationsService operationsService = new OperationsService();
        
        // Use reflection to access the private method
        try {
            java.lang.reflect.Field hmsystemRepoField = OperationsService.class.getDeclaredField("hmsystemRepository");
            hmsystemRepoField.setAccessible(true);
            hmsystemRepoField.set(operationsService, hmsystemRepository);
            
            // Invoke the private method using reflection
            java.lang.reflect.Method method = OperationsService.class.getDeclaredMethod("getCurrentAuditDate");
            method.setAccessible(true);
            LocalDate actualAuditDate = (LocalDate) method.invoke(operationsService);
            
            // Assert
            assertNotNull(actualAuditDate);
            verify(hmsystemRepository, times(1)).findLatestRecord();
        } catch (Exception e) {
            fail("Reflection failed: " + e.getMessage());
        }
    }
}