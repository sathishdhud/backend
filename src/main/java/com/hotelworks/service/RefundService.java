package com.hotelworks.service;

import com.hotelworks.entity.Refund;
import com.hotelworks.repository.RefundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RefundService {
    
    @Autowired
    private RefundRepository refundRepository;
    
    @Autowired
    private NumberGenerationService numberGenerationService;
    
    /**
     * Create a new refund
     */
    public Refund createRefund(Refund refund) {
        // Generate refund ID if not provided
        if (refund.getRefundId() == null || refund.getRefundId().isEmpty()) {
            refund.setRefundId(numberGenerationService.generateCommonReceiptNumber());
        }
        
        // Generate receipt number if not provided
        if (refund.getReceiptNo() == null || refund.getReceiptNo().isEmpty()) {
            refund.setReceiptNo(numberGenerationService.generateCommonReceiptNumber());
        }
        
        return refundRepository.save(refund);
    }
    
    /**
     * Get refund by ID
     */
    public Optional<Refund> getRefundById(String refundId) {
        return refundRepository.findById(refundId);
    }
    
    /**
     * Get all refunds
     */
    public List<Refund> getAllRefunds() {
        return refundRepository.findAll();
    }
    
    /**
     * Get refunds by bill number
     */
    public List<Refund> getRefundsByBillNo(String billNo) {
        return refundRepository.findByBillNo(billNo);
    }
    
    /**
     * Get refunds by folio number
     */
    public List<Refund> getRefundsByFolioNo(String folioNo) {
        return refundRepository.findByFolioNo(folioNo);
    }
    
    /**
     * Get refunds by shift information
     */
    public List<Refund> getRefundsByShift(String shiftNo, java.time.LocalDate shiftDate) {
        return refundRepository.findByShiftNoAndShiftDate(shiftNo, shiftDate);
    }
    
    /**
     * Update refund
     */
    public Refund updateRefund(String refundId, Refund refundDetails) {
        Refund refund = refundRepository.findById(refundId)
            .orElseThrow(() -> new RuntimeException("Refund not found with ID: " + refundId));
        
        refund.setDate(refundDetails.getDate());
        refund.setModeOfPaymentId(refundDetails.getModeOfPaymentId());
        refund.setAmount(refundDetails.getAmount());
        refund.setVoucherNo(refundDetails.getVoucherNo());
        refund.setNarration(refundDetails.getNarration());
        refund.setShiftNo(refundDetails.getShiftNo());
        refund.setShiftDate(refundDetails.getShiftDate());
        refund.setBillNo(refundDetails.getBillNo());
        refund.setFolioNo(refundDetails.getFolioNo());
        refund.setGuestName(refundDetails.getGuestName());
        
        return refundRepository.save(refund);
    }
    
    /**
     * Delete refund
     */
    public void deleteRefund(String refundId) {
        if (!refundRepository.existsById(refundId)) {
            throw new RuntimeException("Refund not found with ID: " + refundId);
        }
        refundRepository.deleteById(refundId);
    }
}