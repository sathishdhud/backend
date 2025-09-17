package com.hotelworks.service;

import com.hotelworks.dto.request.AccountHeadRequest;
import com.hotelworks.dto.response.AccountHeadResponse;
import com.hotelworks.entity.HotelAccountHead;
import com.hotelworks.repository.HotelAccountHeadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AccountHeadService {
    
    @Autowired
    private HotelAccountHeadRepository hotelAccountHeadRepository;
    
    @Autowired
    private NumberGenerationService numberGenerationService;
    
    /**
     * Create a new account head
     */
    public AccountHeadResponse createAccountHead(AccountHeadRequest request) {
        // Generate account head ID if not provided
        String accountHeadId = request.getAccountHeadId();
        if (accountHeadId == null || accountHeadId.trim().isEmpty()) {
            accountHeadId = numberGenerationService.generateAccountHeadId();
        }
        
        // Check if account head already exists
        if (hotelAccountHeadRepository.existsById(accountHeadId)) {
            throw new RuntimeException("Account head already exists: " + accountHeadId);
        }
        
        HotelAccountHead accountHead = new HotelAccountHead();
        accountHead.setAccHeadId(accountHeadId);
        accountHead.setName(request.getAccountName());
        
        HotelAccountHead savedAccountHead = hotelAccountHeadRepository.save(accountHead);
        return mapToAccountHeadResponse(savedAccountHead);
    }
    
    /**
     * Get all account heads
     */
    public List<AccountHeadResponse> getAllAccountHeads() {
        List<HotelAccountHead> accountHeads = hotelAccountHeadRepository.findAll();
        return accountHeads.stream()
            .map(this::mapToAccountHeadResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get account head by ID
     */
    public AccountHeadResponse getAccountHead(String accountHeadId) {
        HotelAccountHead accountHead = hotelAccountHeadRepository.findById(accountHeadId)
            .orElseThrow(() -> new RuntimeException("Account head not found: " + accountHeadId));
        return mapToAccountHeadResponse(accountHead);
    }
    
    /**
     * Update account head
     */
    public AccountHeadResponse updateAccountHead(String accountHeadId, AccountHeadRequest request) {
        HotelAccountHead accountHead = hotelAccountHeadRepository.findById(accountHeadId)
            .orElseThrow(() -> new RuntimeException("Account head not found: " + accountHeadId));
        
        accountHead.setName(request.getAccountName());
        
        HotelAccountHead updatedAccountHead = hotelAccountHeadRepository.save(accountHead);
        return mapToAccountHeadResponse(updatedAccountHead);
    }
    
    /**
     * Delete account head
     */
    public void deleteAccountHead(String accountHeadId) {
        if (!hotelAccountHeadRepository.existsById(accountHeadId)) {
            throw new RuntimeException("Account head not found: " + accountHeadId);
        }
        hotelAccountHeadRepository.deleteById(accountHeadId);
    }
    
    private AccountHeadResponse mapToAccountHeadResponse(HotelAccountHead accountHead) {
        AccountHeadResponse response = new AccountHeadResponse();
        response.setAccountHeadId(accountHead.getAccHeadId());
        response.setAccountName(accountHead.getName());
        return response;
    }
}