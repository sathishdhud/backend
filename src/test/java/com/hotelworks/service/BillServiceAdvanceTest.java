package com.hotelworks.service;

import com.hotelworks.entity.Advance;
import com.hotelworks.entity.CheckIn;
import com.hotelworks.entity.FoBill;
import com.hotelworks.entity.Reservation;
import com.hotelworks.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class BillServiceAdvanceTest {

    @Mock
    private FoBillRepository foBillRepository;

    @Mock
    private CheckInRepository checkInRepository;

    @Mock
    private PostTransactionRepository postTransactionRepository;

    @Mock
    private AdvanceRepository advanceRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private PostTransactionService postTransactionService;

    @Mock
    private AdvanceService advanceService;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private HotelAccountHeadRepository hotelAccountHeadRepository;

    @Mock
    private BillSettlementTypeRepository billSettlementTypeRepository;

    @Mock
    private NumberGenerationService numberGenerationService;

    @InjectMocks
    private BillService billService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGenerateBillWithPreAndPostCheckInAdvances() {
        // Setup test data
        String folioNo = "FOLIO001";
        String reservationNo = "RES001";
        String billNo = "BILL001";
        
        // Create check-in with reservation
        CheckIn checkIn = new CheckIn();
        checkIn.setFolioNo(folioNo);
        checkIn.setReservationNo(reservationNo);
        checkIn.setGuestName("Test Guest");
        checkIn.setRoomId("ROOM001");
        
        // Create reservation with rate
        Reservation reservation = new Reservation();
        reservation.setReservationNo(reservationNo);
        reservation.setGuestName("Test Guest");
        reservation.setRate(new BigDecimal("1000.00"));
        
        // Create bill
        FoBill bill = new FoBill();
        bill.setBillNo(billNo);
        bill.setFolioNo(folioNo);
        bill.setGuestName("Test Guest");
        bill.setRoomId("ROOM001");
        bill.setTotalAmount(new BigDecimal("800.00"));
        bill.setAdvanceAmount(new BigDecimal("300.00"));
        bill.setPaidAmount(new BigDecimal("100.00")); // Only post-check-in advances
        
        // Mock repository responses
        when(checkInRepository.findById(folioNo)).thenReturn(Optional.of(checkIn));
        when(reservationRepository.findById(reservationNo)).thenReturn(Optional.of(reservation));
        when(postTransactionRepository.getTotalTransactionsByFolio(folioNo)).thenReturn(new BigDecimal("800.00"));
        when(advanceRepository.getTotalAdvancesByFolio(folioNo)).thenReturn(new BigDecimal("100.00")); // Folio advances
        when(advanceRepository.getTotalAdvancesByReservation(reservationNo)).thenReturn(new BigDecimal("200.00")); // Reservation advances
        when(numberGenerationService.generateBillNumber()).thenReturn(billNo);
        when(foBillRepository.save(any(FoBill.class))).thenReturn(bill);
        
        // Execute the method
        billService.generateBill(folioNo);
        
        // Verify interactions
        verify(foBillRepository, times(1)).save(any(FoBill.class));
        verify(checkInRepository, times(1)).findById(folioNo);
        verify(reservationRepository, times(1)).findById(reservationNo);
        verify(postTransactionRepository, times(1)).getTotalTransactionsByFolio(folioNo);
        verify(advanceRepository, times(1)).getTotalAdvancesByFolio(folioNo);
        verify(advanceRepository, times(1)).getTotalAdvancesByReservation(reservationNo);
        
        // Verify that total advances include both folio and reservation advances (100 + 200 = 300)
        // And that only post-check-in advances are counted as paid amounts (100)
        verify(foBillRepository).save(argThat(billArg -> 
            billArg.getAdvanceAmount().compareTo(new BigDecimal("300.00")) == 0 &&
            billArg.getPaidAmount().compareTo(new BigDecimal("100.00")) == 0));
    }
    
    @Test
    public void testGenerateBillWithReservationAdvancesOnly() {
        // Setup test data
        String folioNo = "FOLIO002";
        String reservationNo = "RES002";
        String billNo = "BILL002";
        
        // Create check-in with reservation
        CheckIn checkIn = new CheckIn();
        checkIn.setFolioNo(folioNo);
        checkIn.setReservationNo(reservationNo);
        checkIn.setGuestName("Test Guest 2");
        checkIn.setRoomId("ROOM002");
        
        // Create reservation with rate
        Reservation reservation = new Reservation();
        reservation.setReservationNo(reservationNo);
        reservation.setGuestName("Test Guest 2");
        reservation.setRate(new BigDecimal("1000.00"));
        
        // Create bill
        FoBill bill = new FoBill();
        bill.setBillNo(billNo);
        bill.setFolioNo(folioNo);
        bill.setGuestName("Test Guest 2");
        bill.setRoomId("ROOM002");
        bill.setTotalAmount(new BigDecimal("800.00"));
        bill.setAdvanceAmount(new BigDecimal("200.00")); // Only reservation advances
        bill.setPaidAmount(new BigDecimal("0.00")); // No post-check-in advances
        
        // Mock repository responses
        when(checkInRepository.findById(folioNo)).thenReturn(Optional.of(checkIn));
        when(reservationRepository.findById(reservationNo)).thenReturn(Optional.of(reservation));
        when(postTransactionRepository.getTotalTransactionsByFolio(folioNo)).thenReturn(new BigDecimal("800.00"));
        when(advanceRepository.getTotalAdvancesByFolio(folioNo)).thenReturn(new BigDecimal("0.00")); // No folio advances
        when(advanceRepository.getTotalAdvancesByReservation(reservationNo)).thenReturn(new BigDecimal("200.00")); // Reservation advances
        when(numberGenerationService.generateBillNumber()).thenReturn(billNo);
        when(foBillRepository.save(any(FoBill.class))).thenReturn(bill);
        
        // Execute the method
        billService.generateBill(folioNo);
        
        // Verify that total advances include reservation advances (200)
        // And that no advances are counted as paid amounts (0)
        verify(foBillRepository).save(argThat(billArg -> 
            billArg.getAdvanceAmount().compareTo(new BigDecimal("200.00")) == 0 &&
            billArg.getPaidAmount().compareTo(new BigDecimal("0.00")) == 0));
    }
}