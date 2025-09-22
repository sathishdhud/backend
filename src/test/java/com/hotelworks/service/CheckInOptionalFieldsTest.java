package com.hotelworks.service;

import com.hotelworks.dto.request.CheckInRequest;
import com.hotelworks.entity.CheckIn;
import com.hotelworks.entity.Room;
import com.hotelworks.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CheckInOptionalFieldsTest {

    @Mock
    private CheckInRepository checkInRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private AdvanceRepository advanceRepository;

    @Mock
    private ArrivalModeRepository arrivalModeRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private PlanTypeRepository planTypeRepository;

    @Mock
    private RoomTypeRepository roomTypeRepository;

    @Mock
    private BillSettlementTypeRepository settlementTypeRepository;

    @Mock
    private NationalityRepository nationalityRepository;

    @Mock
    private RefModeRepository refModeRepository;

    @Mock
    private ResvSourceRepository resvSourceRepository;

    @Mock
    private ReservationService reservationService;

    @Mock
    private NumberGenerationService numberGenerationService;

    @Mock
    private RoomStatusManagementService roomStatusManagementService;

    @InjectMocks
    private CheckInService checkInService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCheckInWithNullArrivalModeId() {
        // Arrange
        CheckInRequest request = new CheckInRequest();
        request.setRoomId("ROOM001");
        request.setArrivalDate(LocalDate.now());
        request.setDepartureDate(LocalDate.now().plusDays(2));
        request.setMobileNumber("1234567890");
        request.setWalkIn("Y");
        request.setGuestName("Test Guest");
        
        // Make arrivalModeId null (optional)
        request.setArrivalModeId(null);
        
        Room room = new Room();
        room.setRoomId("ROOM001");
        room.setStatus("VR");
        
        CheckIn checkIn = new CheckIn();
        checkIn.setFolioNo("FOLIO001");
        checkIn.setRoomId("ROOM001");
        checkIn.setArrivalDate(LocalDate.now());
        checkIn.setDepartureDate(LocalDate.now().plusDays(2));
        checkIn.setMobileNumber("1234567890");
        checkIn.setWalkIn("Y");
        checkIn.setGuestName("Test Guest");
        checkIn.setArrivalModeId(null);
        
        when(roomRepository.findById("ROOM001")).thenReturn(java.util.Optional.of(room));
        when(roomStatusManagementService.isRoomAvailableForDates("ROOM001", LocalDate.now(), LocalDate.now().plusDays(2))).thenReturn(true);
        when(numberGenerationService.generateFolioNumber()).thenReturn("FOLIO001");
        when(checkInRepository.save(any(CheckIn.class))).thenReturn(checkIn);
        
        // Act & Assert
        // This should not throw any exception even with null arrivalModeId
        assertDoesNotThrow(() -> {
            checkInService.processCheckIn(request);
        });
        
        // Verify that the save method was called
        verify(checkInRepository, times(1)).save(any(CheckIn.class));
    }

    @Test
    public void testCheckInWithEmptyArrivalModeId() {
        // Arrange
        CheckInRequest request = new CheckInRequest();
        request.setRoomId("ROOM001");
        request.setArrivalDate(LocalDate.now());
        request.setDepartureDate(LocalDate.now().plusDays(2));
        request.setMobileNumber("1234567890");
        request.setWalkIn("Y");
        request.setGuestName("Test Guest");
        
        // Make arrivalModeId empty string (should be treated as null)
        request.setArrivalModeId("");
        
        Room room = new Room();
        room.setRoomId("ROOM001");
        room.setStatus("VR");
        
        CheckIn checkIn = new CheckIn();
        checkIn.setFolioNo("FOLIO001");
        checkIn.setRoomId("ROOM001");
        checkIn.setArrivalDate(LocalDate.now());
        checkIn.setDepartureDate(LocalDate.now().plusDays(2));
        checkIn.setMobileNumber("1234567890");
        checkIn.setWalkIn("Y");
        checkIn.setGuestName("Test Guest");
        checkIn.setArrivalModeId("");
        
        when(roomRepository.findById("ROOM001")).thenReturn(java.util.Optional.of(room));
        when(roomStatusManagementService.isRoomAvailableForDates("ROOM001", LocalDate.now(), LocalDate.now().plusDays(2))).thenReturn(true);
        when(numberGenerationService.generateFolioNumber()).thenReturn("FOLIO001");
        when(checkInRepository.save(any(CheckIn.class))).thenReturn(checkIn);
        
        // Act & Assert
        // This should not throw any exception even with empty arrivalModeId
        assertDoesNotThrow(() -> {
            checkInService.processCheckIn(request);
        });
        
        // Verify that the save method was called
        verify(checkInRepository, times(1)).save(any(CheckIn.class));
    }

    @Test
    public void testCheckInWithValidArrivalModeId() {
        // Arrange
        CheckInRequest request = new CheckInRequest();
        request.setRoomId("ROOM001");
        request.setArrivalDate(LocalDate.now());
        request.setDepartureDate(LocalDate.now().plusDays(2));
        request.setMobileNumber("1234567890");
        request.setWalkIn("Y");
        request.setGuestName("Test Guest");
        
        // Set a valid arrivalModeId
        request.setArrivalModeId("WALKIN");
        
        Room room = new Room();
        room.setRoomId("ROOM001");
        room.setStatus("VR");
        
        CheckIn checkIn = new CheckIn();
        checkIn.setFolioNo("FOLIO001");
        checkIn.setRoomId("ROOM001");
        checkIn.setArrivalDate(LocalDate.now());
        checkIn.setDepartureDate(LocalDate.now().plusDays(2));
        checkIn.setMobileNumber("1234567890");
        checkIn.setWalkIn("Y");
        checkIn.setGuestName("Test Guest");
        checkIn.setArrivalModeId("WALKIN");
        
        when(roomRepository.findById("ROOM001")).thenReturn(java.util.Optional.of(room));
        when(roomStatusManagementService.isRoomAvailableForDates("ROOM001", LocalDate.now(), LocalDate.now().plusDays(2))).thenReturn(true);
        when(numberGenerationService.generateFolioNumber()).thenReturn("FOLIO001");
        when(arrivalModeRepository.existsById("WALKIN")).thenReturn(true);
        when(checkInRepository.save(any(CheckIn.class))).thenReturn(checkIn);
        
        // Act & Assert
        // This should not throw any exception with valid arrivalModeId
        assertDoesNotThrow(() -> {
            checkInService.processCheckIn(request);
        });
        
        // Verify that the save method was called
        verify(checkInRepository, times(1)).save(any(CheckIn.class));
        verify(arrivalModeRepository, times(1)).existsById("WALKIN");
    }

    @Test
    public void testCheckInWithInvalidArrivalModeId() {
        // Arrange
        CheckInRequest request = new CheckInRequest();
        request.setRoomId("ROOM001");
        request.setArrivalDate(LocalDate.now());
        request.setDepartureDate(LocalDate.now().plusDays(2));
        request.setMobileNumber("1234567890");
        request.setWalkIn("Y");
        request.setGuestName("Test Guest");
        
        // Set an invalid arrivalModeId
        request.setArrivalModeId("INVALID");
        
        Room room = new Room();
        room.setRoomId("ROOM001");
        room.setStatus("VR");
        
        when(roomRepository.findById("ROOM001")).thenReturn(java.util.Optional.of(room));
        when(roomStatusManagementService.isRoomAvailableForDates("ROOM001", LocalDate.now(), LocalDate.now().plusDays(2))).thenReturn(true);
        when(arrivalModeRepository.existsById("INVALID")).thenReturn(false);
        
        // Act & Assert
        // This should throw an exception with invalid arrivalModeId
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            checkInService.processCheckIn(request);
        });
        
        assertTrue(exception.getMessage().contains("Invalid arrival mode ID"));
        
        // Verify that the save method was not called
        verify(checkInRepository, times(0)).save(any(CheckIn.class));
    }
}