package com.hotelworks.service;

import com.hotelworks.entity.*;
import com.hotelworks.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BillGenerationFlowTest {

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
    public void testCompleteBillGenerationFlow() {
        // Setup test data
        String folioNo = "FOLIO001";
        String reservationNo = "RES001";
        String billNo = "BILL001";
        
        // 1. Create reservation with rate
        Reservation reservation = new Reservation();
        reservation.setReservationNo(reservationNo);
        reservation.setGuestName("Test Guest");
        reservation.setRate(new BigDecimal("1000.00")); // Room charges
        
        // 2. Create check-in
        CheckIn checkIn = new CheckIn();
        checkIn.setFolioNo(folioNo);
        checkIn.setReservationNo(reservationNo);
        checkIn.setGuestName("Test Guest");
        checkIn.setRoomId("ROOM001");
        checkIn.setReservation(reservation);
        
        // 3. Create reservation advances (before check-in)
        List<Advance> reservationAdvancesList = new ArrayList<>();
        Advance reservationAdvance = new Advance();
        reservationAdvance.setReceiptNo("ADV001");
        reservationAdvance.setReservationNo(reservationNo);
        reservationAdvance.setAmount(new BigDecimal("500.00"));
        reservationAdvancesList.add(reservationAdvance);
        
        // 4. Create folio advances (after check-in)
        List<Advance> folioAdvancesList = new ArrayList<>();
        Advance folioAdvance = new Advance();
        folioAdvance.setReceiptNo("ADV002");
        folioAdvance.setFolioNo(folioNo);
        folioAdvance.setAmount(new BigDecimal("300.00"));
        folioAdvancesList.add(folioAdvance);
        
        // 5. Create transactions (after check-in)
        List<PostTransaction> transactionsList = new ArrayList<>();
        PostTransaction transaction = new PostTransaction();
        transaction.setTransactionId("TRANS001");
        transaction.setFolioNo(folioNo);
        transaction.setAmount(new BigDecimal("200.00"));
        transactionsList.add(transaction);
        
        // 6. Create bill
        FoBill bill = new FoBill();
        bill.setBillNo(billNo);
        bill.setFolioNo(folioNo);
        bill.setGuestName("Test Guest");
        bill.setRoomId("ROOM001");
        bill.setTotalAmount(new BigDecimal("200.00")); // Transactions
        bill.setAdvanceAmount(new BigDecimal("800.00")); // Reservation (500) + Folio (300)
        bill.setPaidAmount(new BigDecimal("300.00")); // Only folio advances count as paid initially
        
        // Mock repository responses
        when(checkInRepository.findById(folioNo)).thenReturn(Optional.of(checkIn));
        when(reservationRepository.findById(reservationNo)).thenReturn(Optional.of(reservation));
        when(postTransactionRepository.getTotalTransactionsByFolio(folioNo)).thenReturn(new BigDecimal("200.00"));
        when(advanceRepository.getTotalAdvancesByFolio(folioNo)).thenReturn(new BigDecimal("300.00"));
        when(advanceRepository.getTotalAdvancesByReservation(reservationNo)).thenReturn(new BigDecimal("500.00"));
        when(numberGenerationService.generateBillNumber()).thenReturn(billNo);
        when(foBillRepository.save(any(FoBill.class))).thenReturn(bill);
        when(advanceRepository.findByFolioNo(folioNo)).thenReturn(folioAdvancesList);
        when(advanceRepository.findByReservationNo(reservationNo)).thenReturn(reservationAdvancesList);
        when(postTransactionRepository.findByFolioNo(folioNo)).thenReturn(transactionsList);
        
        // Execute the method
        billService.generateBill(folioNo);
        
        // Verify interactions
        verify(foBillRepository, times(1)).save(any(FoBill.class));
        verify(checkInRepository, times(1)).findById(folioNo);
        verify(reservationRepository, times(1)).findById(reservationNo);
        verify(postTransactionRepository, times(1)).getTotalTransactionsByFolio(folioNo);
        verify(advanceRepository, times(1)).getTotalAdvancesByFolio(folioNo);
        verify(advanceRepository, times(1)).getTotalAdvancesByReservation(reservationNo);
        verify(advanceRepository, times(1)).findByFolioNo(folioNo);
        verify(advanceRepository, times(1)).findByReservationNo(reservationNo);
        verify(postTransactionRepository, times(1)).findByFolioNo(folioNo);
        
        // Verify that the bill was created with correct values:
        // Total billable = Room charges (1000) + Transactions (200) = 1200
        // Total advances = Reservation advances (500) + Folio advances (300) = 800
        // Paid amount = Folio advances (300)
        // Balance = 1200 - (800 + 300) = 100
        verify(foBillRepository).save(argThat(billArg -> 
            billArg.getTotalAmount().compareTo(new BigDecimal("200.00")) == 0 &&
            billArg.getAdvanceAmount().compareTo(new BigDecimal("800.00")) == 0 &&
            billArg.getPaidAmount().compareTo(new BigDecimal("300.00")) == 0));
    }
    
    @Test
    public void testBalanceCalculationAfterPayment() {
        // Create test objects
        Reservation reservation = new Reservation();
        reservation.setRate(new BigDecimal("1000.00")); // Room charges
        
        CheckIn checkIn = new CheckIn();
        checkIn.setReservation(reservation);
        
        FoBill bill = new FoBill();
        bill.setCheckIn(checkIn);
        bill.setTotalAmount(new BigDecimal("200.00")); // Additional transactions
        bill.setAdvanceAmount(new BigDecimal("800.00")); // Total advances
        bill.setPaidAmount(new BigDecimal("500.00")); // Payments made
        
        // Calculate balance
        bill.calculateBalanceAmount();
        
        // Expected calculation:
        // Total billable = Room charges (1000) + Additional transactions (200) = 1200
        // Total paid = Advances (800) + Payments (500) = 1300
        // Balance = 1200 - 1300 = -100, but should be 0 (can't be negative)
        assertEquals(BigDecimal.ZERO, bill.getBalanceAmount());
    }
    
    @Test
    public void testBalanceCalculationWithOutstandingAmount() {
        // Create test objects
        Reservation reservation = new Reservation();
        reservation.setRate(new BigDecimal("1000.00")); // Room charges
        
        CheckIn checkIn = new CheckIn();
        checkIn.setReservation(reservation);
        
        FoBill bill = new FoBill();
        bill.setCheckIn(checkIn);
        bill.setTotalAmount(new BigDecimal("300.00")); // Additional transactions
        bill.setAdvanceAmount(new BigDecimal("800.00")); // Total advances
        bill.setPaidAmount(new BigDecimal("200.00")); // Payments made
        
        // Calculate balance
        bill.calculateBalanceAmount();
        
        // Expected calculation:
        // Total billable = Room charges (1000) + Additional transactions (300) = 1300
        // Total paid = Advances (800) + Payments (200) = 1000
        // Balance = 1300 - 1000 = 300
        assertEquals(new BigDecimal("300.00"), bill.getBalanceAmount());
    }
}