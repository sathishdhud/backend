package com.hotelworks.service;

import com.hotelworks.entity.CheckIn;
import com.hotelworks.repository.CheckInRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
public class CheckInServiceTest {

    @MockBean
    private CheckInRepository checkInRepository;

    @Test
    public void testFindInHouseGuestsFiltersCheckedOutGuests() {
        // Arrange
        LocalDate currentDate = LocalDate.now();
        
        // Create a checked-in guest (not checked out)
        CheckIn inHouseGuest = new CheckIn();
        inHouseGuest.setFolioNo("F1-25-26");
        inHouseGuest.setGuestName("John Doe");
        inHouseGuest.setRoomId("ROOM001");
        inHouseGuest.setArrivalDate(currentDate.minusDays(2));
        inHouseGuest.setDepartureDate(currentDate.plusDays(3));
        inHouseGuest.setCheckout(false);
        
        // Create a checked-out guest
        CheckIn checkedOutGuest = new CheckIn();
        checkedOutGuest.setFolioNo("F2-25-26");
        checkedOutGuest.setGuestName("Jane Smith");
        checkedOutGuest.setRoomId("ROOM002");
        checkedOutGuest.setArrivalDate(currentDate.minusDays(5));
        checkedOutGuest.setDepartureDate(currentDate.minusDays(2));
        checkedOutGuest.setCheckout(true);
        
        // Mock repository to return both guests
        when(checkInRepository.findInHouseGuests()).thenReturn(Arrays.asList(inHouseGuest));
        
        // Verify that only the in-house guest is returned (not the checked-out one)
        List<CheckIn> result = checkInRepository.findInHouseGuests();
        
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getGuestName());
        assertFalse(result.get(0).getCheckout());
        
        verify(checkInRepository, times(1)).findInHouseGuests();
    }
}