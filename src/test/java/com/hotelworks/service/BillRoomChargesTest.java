package com.hotelworks.service;

import com.hotelworks.entity.*;
import com.hotelworks.repository.*;
import com.hotelworks.dto.response.BillResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BillRoomChargesTest {

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
    public void testBillResponseSeparatesRoomChargesAndTransactions() {
        // Setup test data
        String folioNo = "FOLIO001";
        String reservationNo = "RES001";
        String billNo = "BILL001";
        
        // Create reservation with rate (room charges)
        Reservation reservation = new Reservation();
        reservation.setReservationNo(reservationNo);
        reservation.setRate(new BigDecimal("700.00")); // Room charges
        
        // Create check-in
        CheckIn checkIn = new CheckIn();
        checkIn.setFolioNo(folioNo);
        checkIn.setReservationNo(reservationNo);
        checkIn.setReservation(reservation);
        
        // Create bill
        FoBill bill = new FoBill();
        bill.setBillNo(billNo);
        bill.setFolioNo(folioNo);
        bill.setGuestName("Test Guest");
        bill.setRoomId("ROOM001");
        bill.setTotalAmount(new BigDecimal("700.00")); // Transaction amount (food, services)
        bill.setAdvanceAmount(new BigDecimal("500.00")); // Advances
        bill.setPaidAmount(new BigDecimal("300.00")); // Payments
        bill.setCheckIn(checkIn);
        
        // Create mock transactions
        List<com.hotelworks.dto.response.PostTransactionResponse> transactions = new ArrayList<>();
        com.hotelworks.dto.response.PostTransactionResponse transaction1 = new com.hotelworks.dto.response.PostTransactionResponse();
        transaction1.setAmount(new BigDecimal("400.00"));
        transactions.add(transaction1);
        
        com.hotelworks.dto.response.PostTransactionResponse transaction2 = new com.hotelworks.dto.response.PostTransactionResponse();
        transaction2.setAmount(new BigDecimal("300.00"));
        transactions.add(transaction2);
        
        // Create mock advances
        List<com.hotelworks.dto.response.AdvanceResponse> advances = new ArrayList<>();
        com.hotelworks.dto.response.AdvanceResponse advance1 = new com.hotelworks.dto.response.AdvanceResponse();
        advance1.setAmount(new BigDecimal("300.00"));
        advances.add(advance1);
        
        com.hotelworks.dto.response.AdvanceResponse advance2 = new com.hotelworks.dto.response.AdvanceResponse();
        advance2.setAmount(new BigDecimal("200.00"));
        advances.add(advance2);
        
        // Mock repository responses
        when(foBillRepository.findById(billNo)).thenReturn(Optional.of(bill));
        when(postTransactionService.getTransactionsByBill(billNo)).thenReturn(transactions);
        when(advanceService.getAdvancesByBill(billNo)).thenReturn(advances);
        when(roomRepository.findById("ROOM001")).thenReturn(Optional.empty());
        
        // Execute the method
        BillResponse response = billService.getBill(billNo);
        
        // Verify the response properly separates room charges and transactions:
        // Room charges: 700 (from reservation)
        // Transaction amount: 700 (from transactions)
        // Total billable: 1400 (700 + 700)
        // Advances: 500
        // Payments: 300
        // Balance: 1400 - (500 + 300) = 600
        
        assertEquals(new BigDecimal("700.00"), response.getRoomCharges());
        assertEquals(new BigDecimal("700.00"), response.getTotalTransactionAmount());
        assertEquals(new BigDecimal("600.00"), response.getBalanceAmount());
    }
}