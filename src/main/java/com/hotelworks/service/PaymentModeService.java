package com.hotelworks.service;

import com.hotelworks.dto.request.PaymentModeRequest;
import com.hotelworks.dto.response.PaymentModeResponse;
import com.hotelworks.entity.BillSettlementType;
import com.hotelworks.repository.BillSettlementTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentModeService {
    
    @Autowired
    private BillSettlementTypeRepository billSettlementTypeRepository;
    
    /**
     * Create a new payment mode
     */
    public PaymentModeResponse createPaymentMode(PaymentModeRequest request) {
        // Check if payment mode already exists
        if (billSettlementTypeRepository.existsById(request.getId())) {
            throw new RuntimeException("Payment mode already exists: " + request.getId());
        }
        
        BillSettlementType paymentMode = new BillSettlementType();
        paymentMode.setId(request.getId());
        paymentMode.setName(request.getName());
        
        BillSettlementType savedPaymentMode = billSettlementTypeRepository.save(paymentMode);
        return mapToPaymentModeResponse(savedPaymentMode);
    }
    
    /**
     * Get all payment modes
     */
    public List<PaymentModeResponse> getAllPaymentModes() {
        List<BillSettlementType> paymentModes = billSettlementTypeRepository.findAll();
        return paymentModes.stream()
            .map(this::mapToPaymentModeResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get payment mode by ID
     */
    public PaymentModeResponse getPaymentMode(String id) {
        BillSettlementType paymentMode = billSettlementTypeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Payment mode not found: " + id));
        return mapToPaymentModeResponse(paymentMode);
    }
    
    /**
     * Update payment mode
     */
    public PaymentModeResponse updatePaymentMode(String id, PaymentModeRequest request) {
        BillSettlementType paymentMode = billSettlementTypeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Payment mode not found: " + id));
        
        paymentMode.setName(request.getName());
        BillSettlementType updatedPaymentMode = billSettlementTypeRepository.save(paymentMode);
        return mapToPaymentModeResponse(updatedPaymentMode);
    }
    
    /**
     * Delete payment mode
     */
    public void deletePaymentMode(String id) {
        if (!billSettlementTypeRepository.existsById(id)) {
            throw new RuntimeException("Payment mode not found: " + id);
        }
        billSettlementTypeRepository.deleteById(id);
    }
    
    private PaymentModeResponse mapToPaymentModeResponse(BillSettlementType paymentMode) {
        PaymentModeResponse response = new PaymentModeResponse();
        response.setId(paymentMode.getId());
        response.setName(paymentMode.getName());
        return response;
    }
}