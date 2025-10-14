package com.hotelworks.service;

import com.hotelworks.dto.request.ShiftCloseRequest;
import com.hotelworks.entity.Advance;
import com.hotelworks.entity.Hmsystem;
import com.hotelworks.entity.PostTransaction;
import com.hotelworks.entity.Refund;
import com.hotelworks.entity.SalesReceipt;
import com.hotelworks.repository.AdvanceRepository;
import com.hotelworks.repository.HmsystemRepository;
import com.hotelworks.repository.PostTransactionRepository;
import com.hotelworks.repository.RefundRepository;
import com.hotelworks.repository.SalesReceiptRepository;
import com.hotelworks.repository.ShiftRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class ShiftCloseFunctionalityTest {

    @Autowired
    private OperationsService operationsService;
    
    @Autowired
    private HmsystemRepository hmsystemRepository;
    
    @Autowired
    private ShiftRepository shiftRepository;
    
    @Autowired
    private AdvanceRepository advanceRepository;
    
    @Autowired
    private SalesReceiptRepository salesReceiptRepository;
    
    @Autowired
    private PostTransactionRepository postTransactionRepository;
    
    @Autowired
    private RefundRepository refundRepository;
    
    @Autowired
    private NumberGenerationService numberGenerationService;

    @BeforeEach
    public void setUp() {
        // Clean up repositories
        shiftRepository.deleteAll();
        hmsystemRepository.deleteAll();
        
        // Create initial HMS system record
        Hmsystem hmsystem = new Hmsystem();
        hmsystem.setShiftDate(LocalDate.now());
        hmsystem.setRunningShift(1);
        hmsystem.setTotalShift(3);
        hmsystemRepository.save(hmsystem);
        
        // Create test cash advance
        Advance advance = new Advance();
        advance.setReceiptNo(numberGenerationService.generateCommonReceiptNumber());
        advance.setGuestName("Test Guest");
        advance.setDate(LocalDate.now());
        advance.setShiftNo("1");
        advance.setShiftDate(LocalDate.now());
        advance.setModeOfPaymentId("CASH");
        advance.setAmount(new BigDecimal("1000.00"));
        advanceRepository.save(advance);
        
        // Create test cash sales receipt
        SalesReceipt salesReceipt = new SalesReceipt();
        salesReceipt.setReceiptNo(numberGenerationService.generateCommonReceiptNumber());
        salesReceipt.setDate(LocalDate.now());
        salesReceipt.setModeOfPaymentId("CASH");
        salesReceipt.setAmount(new BigDecimal("500.00"));
        salesReceipt.setShiftNo("1");
        salesReceipt.setShiftDate(LocalDate.now());
        salesReceiptRepository.save(salesReceipt);
        
        // Create test expense
        PostTransaction expense = new PostTransaction();
        expense.setTransactionId(numberGenerationService.generateTransactionId());
        expense.setGuestName("Test Guest");
        expense.setDate(LocalDate.now());
        expense.setAccHeadId("EXPENSE");
        expense.setAmount(new BigDecimal("200.00"));
        expense.setShiftNo("1");
        expense.setShiftDate(LocalDate.now());
        postTransactionRepository.save(expense);
        
        // Create test refund
        Refund refund = new Refund();
        refund.setRefundId(numberGenerationService.generateCommonReceiptNumber());
        refund.setReceiptNo(numberGenerationService.generateCommonReceiptNumber());
        refund.setDate(LocalDate.now());
        refund.setModeOfPaymentId("CASH");
        refund.setAmount(new BigDecimal("100.00"));
        refund.setShiftNo("1");
        refund.setShiftDate(LocalDate.now());
        refund.setGuestName("Test Guest");
        refundRepository.save(refund);
    }

    @Test
    public void testShiftCloseWithProperFinancialCalculation() {
        // Create shift close request
        ShiftCloseRequest request = new ShiftCloseRequest();
        request.setOpeningBalance(new BigDecimal("5000.00"));
        request.setClosingBalance(new BigDecimal("0.00")); // This will be calculated
        request.setTotalIncome(new BigDecimal("0.00")); // This will be calculated
        request.setTotalExpense(new BigDecimal("0.00")); // This will be calculated
        
        // Process shift close
        String result = operationsService.processShiftClose(request);
        
        // Verify the result contains the proper calculation
        assertTrue(result.contains("Shift 1 closed successfully"));
        assertTrue(result.contains("Receipts: 1500.00")); // 1000 (advance) + 500 (sales) = 1500
        assertTrue(result.contains("Expenses: 200.00"));
        assertTrue(result.contains("Refunds: 100.00"));
        
        // Expected closing balance calculation:
        // (Opening: 5000) + (Receipts: 1500) - (Expenses: 200 + Refunds: 100) = 6200
        assertTrue(result.contains("Calculated closing balance: 6200"));
        
        // Verify shift record was created
        assertEquals(1, shiftRepository.count());
        
        // Get the created shift record
        var shifts = shiftRepository.findAll();
        var shift = shifts.get(0);
        
        // Verify financial figures in the shift record
        assertEquals(new BigDecimal("5000.00"), shift.getOpeningBalance());
        assertEquals(new BigDecimal("6200.00"), shift.getClosingBalance());
        assertEquals(new BigDecimal("1500.00"), shift.getTotalIncome());
        assertEquals(new BigDecimal("300.00"), shift.getTotalExpense()); // 200 (expense) + 100 (refund) = 300
    }
    
    @Test
    public void testShiftCloseWithZeroValues() {
        // Delete all test data to test with zero values
        advanceRepository.deleteAll();
        salesReceiptRepository.deleteAll();
        postTransactionRepository.deleteAll();
        refundRepository.deleteAll();
        
        // Create shift close request with zero values
        ShiftCloseRequest request = new ShiftCloseRequest();
        request.setOpeningBalance(new BigDecimal("1000.00"));
        request.setClosingBalance(new BigDecimal("0.00"));
        request.setTotalIncome(new BigDecimal("0.00"));
        request.setTotalExpense(new BigDecimal("0.00"));
        
        // Process shift close
        String result = operationsService.processShiftClose(request);
        
        // With zero receipts, expenses, and refunds, closing balance should equal opening balance
        assertTrue(result.contains("Calculated closing balance: 1000"));
        
        // Verify shift record was created
        assertEquals(1, shiftRepository.count());
        
        // Get the created shift record
        var shifts = shiftRepository.findAll();
        var shift = shifts.get(0);
        
        // Verify financial figures in the shift record
        assertEquals(new BigDecimal("1000.00"), shift.getOpeningBalance());
        assertEquals(new BigDecimal("1000.00"), shift.getClosingBalance());
        assertEquals(new BigDecimal("0.00"), shift.getTotalIncome());
        assertEquals(new BigDecimal("0.00"), shift.getTotalExpense());
    }
    
    @Test
    public void testHmsystemShiftRotation() {
        // Get initial HMS system state
        Optional<Hmsystem> initialHmsystemOpt = hmsystemRepository.findLatestRecord();
        assertTrue(initialHmsystemOpt.isPresent());
        Hmsystem initialHmsystem = initialHmsystemOpt.get();
        assertEquals(1, initialHmsystem.getRunningShift());
        assertEquals(3, initialHmsystem.getTotalShift());
        
        // Close all 3 shifts to test rotation
        for (int i = 1; i <= 3; i++) {
            ShiftCloseRequest request = new ShiftCloseRequest();
            request.setOpeningBalance(new BigDecimal("1000.00"));
            request.setClosingBalance(new BigDecimal("0.00"));
            request.setTotalIncome(new BigDecimal("0.00"));
            request.setTotalExpense(new BigDecimal("0.00"));
            
            String result = operationsService.processShiftClose(request);
            System.out.println("Shift " + i + " close result: " + result);
        }
        
        // After closing 3 shifts, we should have 3 shift records
        assertEquals(3, shiftRepository.count());
        
        // Get final HMS system state
        Optional<Hmsystem> finalHmsystemOpt = hmsystemRepository.findLatestRecord();
        assertTrue(finalHmsystemOpt.isPresent());
        Hmsystem finalHmsystem = finalHmsystemOpt.get();
        
        // After closing shift 3, the date should have advanced and running shift reset to 1
        assertEquals(initialHmsystem.getShiftDate().plusDays(1), finalHmsystem.getShiftDate());
        assertEquals(1, finalHmsystem.getRunningShift());
        assertEquals(3, finalHmsystem.getTotalShift());
    }
}