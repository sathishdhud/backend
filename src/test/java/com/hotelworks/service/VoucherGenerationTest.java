package com.hotelworks.service;

import com.hotelworks.entity.PostTransaction;
import com.hotelworks.repository.PostTransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class VoucherGenerationTest {

    @Autowired
    private NumberGenerationService numberGenerationService;

    @MockBean
    private PostTransactionRepository postTransactionRepository;

    @Test
    public void testTransactionVoucherNumberGeneration() {
        // Test that voucher numbers are generated in the correct format
        String voucherNo = numberGenerationService.generateTransactionVoucherNumber();
        
        // Pattern should match T1-25-26 format (or similar accounting year)
        Pattern pattern = Pattern.compile("T\\d+-\\d{2}-\\d{2}");
        assertTrue(pattern.matcher(voucherNo).matches(), "Voucher number should match pattern T\\d+-\\d{2}-\\d{2}");
        
        System.out.println("Generated transaction voucher number: " + voucherNo);
    }

    @Test
    public void testExpenseVoucherNumberGeneration() {
        // Test that expense voucher numbers are generated in the correct format
        String voucherNo = numberGenerationService.generateExpenseVoucherNumber();
        
        // Pattern should match E1-25-26 format (or similar accounting year)
        Pattern pattern = Pattern.compile("E\\d+-\\d{2}-\\d{2}");
        assertTrue(pattern.matcher(voucherNo).matches(), "Expense voucher number should match pattern E\\d+-\\d{2}-\\d{2}");
        
        System.out.println("Generated expense voucher number: " + voucherNo);
    }

    @Test
    public void testPostTransactionWithAuditDate() {
        // Test that PostTransaction entities use audit date correctly
        PostTransaction transaction = new PostTransaction();
        LocalDate auditDate = LocalDate.now();
        
        transaction.setDate(auditDate); // Set voucher date to audit date
        transaction.setAuditDate(auditDate); // Set audit date
        
        assertEquals(auditDate, transaction.getDate(), "Voucher date should be audit date");
        assertEquals(auditDate, transaction.getAuditDate(), "Audit date should be set correctly");
    }
}