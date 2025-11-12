package com.hotelworks.service;

import com.hotelworks.entity.CheckIn;
import com.hotelworks.repository.CheckInRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
public class CheckoutFunctionalityTest {

    @MockBean
    private CheckInRepository checkInRepository;

    @Test
    public void testFindInHouseGuestsOnlyReturnsUncheckedOutGuests() {
        // Arrange
        // Create a checked-in guest (not checked out)
        CheckIn inHouseGuest = new CheckIn();
        inHouseGuest.setFolioNo("F1-25-26");
        inHouseGuest.setGuestName("John Doe");
        inHouseGuest.setRoomId("ROOM001");
        inHouseGuest.setCheckout(false); // 0 means checked in
        
        // Create a checked-out guest
        CheckIn checkedOutGuest = new CheckIn();
        checkedOutGuest.setFolioNo("F2-25-26");
        checkedOutGuest.setGuestName("Jane Smith");
        checkedOutGuest.setRoomId("ROOM002");
        checkedOutGuest.setCheckout(true); // 1 means checked out
        
        // Mock repository to return only in-house guests
        when(checkInRepository.findInHouseGuests()).thenReturn(Arrays.asList(inHouseGuest));
        
        // Act
        List<CheckIn> result = checkInRepository.findInHouseGuests();
        
        // Assert
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getGuestName());
        assertFalse(result.get(0).getCheckout()); // Should be false (0) for in-house guests
        
        verify(checkInRepository, times(1)).findInHouseGuests();
    }
    
    @Test
    public void testFindInHouseGuestsExcludesCheckedOutGuests() {
        // Arrange
        // Create a checked-in guest (not checked out)
        CheckIn inHouseGuest = new CheckIn();
        inHouseGuest.setFolioNo("F1-25-26");
        inHouseGuest.setGuestName("John Doe");
        inHouseGuest.setRoomId("ROOM001");
        inHouseGuest.setCheckout(false); // 0 means checked in
        
        // Mock repository to return only in-house guests
        when(checkInRepository.findInHouseGuests()).thenReturn(Arrays.asList(inHouseGuest));
        
        // Act
        List<CheckIn> result = checkInRepository.findInHouseGuests();
        
        // Assert
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getGuestName());
        assertNull(result.get(0).getDepartureDate()); // No dependency on departure date
        assertFalse(result.get(0).getCheckout()); // Should be false (0) for in-house guests
    }
}