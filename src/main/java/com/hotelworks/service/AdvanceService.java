package com.hotelworks.service;

import com.hotelworks.dto.request.AdvanceRequest;
import com.hotelworks.dto.response.AdvanceResponse;
import com.hotelworks.dto.response.AdvanceSummaryResponse;
import com.hotelworks.entity.Advance;
import com.hotelworks.entity.CheckIn;
import com.hotelworks.entity.Reservation;
import com.hotelworks.entity.FoBill;
import com.hotelworks.entity.Room;
import com.hotelworks.repository.AdvanceRepository;
import com.hotelworks.repository.CheckInRepository;
import com.hotelworks.repository.ReservationRepository;
import com.hotelworks.repository.FoBillRepository;
import com.hotelworks.repository.BillSettlementTypeRepository;
import com.hotelworks.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdvanceService {
    
    @Autowired
    private AdvanceRepository advanceRepository;
    
    @Autowired
    private CheckInRepository checkInRepository;
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private FoBillRepository foBillRepository;
    
    @Autowired
    private BillSettlementTypeRepository billSettlementTypeRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private NumberGenerationService numberGenerationService;
    
    /**
     * Create advance for reservation
     */
    public AdvanceResponse createAdvanceForReservation(AdvanceRequest request) {
        validateAdvanceRequest(request);
        
        if (request.getReservationNo() == null) {
            throw new RuntimeException("Reservation number is required for reservation advance");
        }
        
        if (!reservationRepository.existsById(request.getReservationNo())) {
            throw new RuntimeException("Reservation not found: " + request.getReservationNo());
        }
        
        Advance advance = createAdvanceEntity(request);
        advance.setReservationNo(request.getReservationNo());
        
        Advance savedAdvance = advanceRepository.save(advance);
        return mapToAdvanceResponse(savedAdvance);
    }
    
    /**
     * Create advance for in-house guest
     */
    public AdvanceResponse createAdvanceForInHouseGuest(AdvanceRequest request) {
        validateAdvanceRequest(request);
        
        if (request.getFolioNo() == null) {
            throw new RuntimeException("Folio number is required for in-house guest advance");
        }
        
        if (!checkInRepository.existsById(request.getFolioNo())) {
            throw new RuntimeException("Check-in not found: " + request.getFolioNo());
        }
        
        Advance advance = createAdvanceEntity(request);
        advance.setFolioNo(request.getFolioNo());
        
        Advance savedAdvance = advanceRepository.save(advance);
        return mapToAdvanceResponse(savedAdvance);
    }
    
    /**
     * Create advance for checkout guest
     */
    public AdvanceResponse createAdvanceForCheckoutGuest(AdvanceRequest request) {
        validateAdvanceRequest(request);
        
        if (request.getBillNo() == null) {
            throw new RuntimeException("Bill number is required for checkout guest advance");
        }
        
        if (!foBillRepository.existsById(request.getBillNo())) {
            throw new RuntimeException("Bill not found: " + request.getBillNo());
        }
        
        if (request.getDate() == null) {
            throw new RuntimeException("Date is required for checkout guest advance");
        }
        
        Advance advance = createAdvanceEntity(request);
        advance.setBillNo(request.getBillNo());
        advance.setDate(request.getDate()); // Manual date entry for checkout guests
        
        Advance savedAdvance = advanceRepository.save(advance);
        return mapToAdvanceResponse(savedAdvance);
    }
    
    /**
     * Get advances by reservation number
     */
    public List<AdvanceResponse> getAdvancesByReservation(String reservationNo) {
        List<Advance> advances = advanceRepository.findByReservationNo(reservationNo);
        return advances.stream()
            .map(this::mapToAdvanceResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get advances by folio number
     */
    public List<AdvanceResponse> getAdvancesByFolio(String folioNo) {
        List<Advance> advances = advanceRepository.findByFolioNo(folioNo);
        return advances.stream()
            .map(this::mapToAdvanceResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get advances by bill number
     */
    public List<AdvanceResponse> getAdvancesByBill(String billNo) {
        List<Advance> advances = advanceRepository.findByBillNo(billNo);
        return advances.stream()
            .map(this::mapToAdvanceResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get total advances by reservation
     */
    public BigDecimal getTotalAdvancesByReservation(String reservationNo) {
        BigDecimal total = advanceRepository.getTotalAdvancesByReservation(reservationNo);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    /**
     * Get total advances by folio
     */
    public BigDecimal getTotalAdvancesByFolio(String folioNo) {
        BigDecimal total = advanceRepository.getTotalAdvancesByFolio(folioNo);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    /**
     * Get total advances by bill
     */
    public BigDecimal getTotalAdvancesByBill(String billNo) {
        BigDecimal total = advanceRepository.getTotalAdvancesByBill(billNo);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    /**
     * Get advance summary by reservation number
     */
    public AdvanceSummaryResponse getAdvanceSummaryByReservation(String reservationNo) {
        // Validate reservation exists
        Reservation reservation = reservationRepository.findById(reservationNo)
            .orElseThrow(() -> new RuntimeException("Reservation not found: " + reservationNo));
        
        // Get all advances for this reservation
        List<Advance> advances = advanceRepository.findByReservationNo(reservationNo);
        
        // Create summary response
        AdvanceSummaryResponse summary = new AdvanceSummaryResponse();
        summary.setReferenceNumber(reservationNo);
        summary.setGuestName(reservation.getGuestName());
        summary.setArrivalDate(reservation.getArrivalDate());
        summary.setDepartureDate(reservation.getDepartureDate());
        
        // Try to get room information from check-in if exists
        checkInRepository.findByReservationNo(reservationNo).ifPresent(checkIn -> {
            if (checkIn.getRoomId() != null) {
                roomRepository.findById(checkIn.getRoomId()).ifPresent(room -> {
                    summary.setRoomNo(room.getRoomNo());
                });
            }
        });
        
        // Calculate totals
        BigDecimal totalAmount = advances.stream()
            .map(Advance::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        summary.setTotalAmount(totalAmount);
        summary.setTotalCount(advances.size());
        
        // Map advances to detail objects
        List<AdvanceSummaryResponse.AdvanceDetail> details = advances.stream()
            .map(this::mapToAdvanceDetail)
            .collect(Collectors.toList());
        
        summary.setAdvanceDetails(details);
        
        return summary;
    }
    
    /**
     * Get advance summary by bill number
     */
    public AdvanceSummaryResponse getAdvanceSummaryByBill(String billNo) {
        // Validate bill exists
        FoBill bill = foBillRepository.findById(billNo)
            .orElseThrow(() -> new RuntimeException("Bill not found: " + billNo));
        
        // Get all advances for this bill
        List<Advance> advances = advanceRepository.findByBillNo(billNo);
        
        // Create summary response
        AdvanceSummaryResponse summary = new AdvanceSummaryResponse();
        summary.setReferenceNumber(billNo);
        summary.setGuestName(bill.getGuestName());
        
        // Try to get dates and room from check-in if exists
        if (bill.getFolioNo() != null) {
            checkInRepository.findById(bill.getFolioNo()).ifPresent(checkIn -> {
                summary.setArrivalDate(checkIn.getArrivalDate());
                summary.setDepartureDate(checkIn.getDepartureDate());
                if (checkIn.getRoomId() != null) {
                    roomRepository.findById(checkIn.getRoomId()).ifPresent(room -> {
                        summary.setRoomNo(room.getRoomNo());
                    });
                }
            });
        }
        
        // Calculate totals
        BigDecimal totalAmount = advances.stream()
            .map(Advance::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        summary.setTotalAmount(totalAmount);
        summary.setTotalCount(advances.size());
        
        // Map advances to detail objects
        List<AdvanceSummaryResponse.AdvanceDetail> details = advances.stream()
            .map(this::mapToAdvanceDetail)
            .collect(Collectors.toList());
        
        summary.setAdvanceDetails(details);
        
        return summary;
    }
    
    /**
     * Get advance by ID
     */
    public AdvanceResponse getAdvance(String advanceId) {
        Advance advance = advanceRepository.findById(advanceId)
            .orElseThrow(() -> new RuntimeException("Advance not found: " + advanceId));
        return mapToAdvanceResponse(advance);
    }
    
    /**
     * Update advance
     */
    public AdvanceResponse updateAdvance(String advanceId, AdvanceRequest request) {
        Advance advance = advanceRepository.findById(advanceId)
            .orElseThrow(() -> new RuntimeException("Advance not found: " + advanceId));
        
        validateAdvanceRequest(request);
        
        // Update editable fields
        advance.setGuestName(request.getGuestName());
        advance.setAmount(request.getAmount());
        advance.setModeOfPaymentId(request.getModeOfPaymentId());
        advance.setCreditCardCompany(request.getCreditCardCompany());
        advance.setCardNumber(request.getCardNumber());
        advance.setOnlineCompanyName(request.getOnlineCompanyName());
        advance.setDetails(request.getDetails());
        advance.setNarration(request.getNarration());
        
        // Update date if provided
        if (request.getDate() != null) {
            advance.setDate(request.getDate());
        }
        
        // Update arrival date if provided
        if (request.getArrivalDate() != null) {
            advance.setArrivalDate(request.getArrivalDate());
        }
        
        Advance savedAdvance = advanceRepository.save(advance);
        return mapToAdvanceResponse(savedAdvance);
    }
    
    /**
     * Delete advance
     */
    public void deleteAdvance(String advanceId) {
        if (!advanceRepository.existsById(advanceId)) {
            throw new RuntimeException("Advance not found: " + advanceId);
        }
        advanceRepository.deleteById(advanceId);
    }
    
    /**
     * Get guest name by bill number
     */
    public String getGuestNameByBill(String billNo) {
        FoBill bill = foBillRepository.findById(billNo)
            .orElseThrow(() -> new RuntimeException("Bill not found: " + billNo));
        return bill.getGuestName();
    }

    /**
     * Get guest name by reservation number
     */
    public String getGuestNameByReservation(String reservationNo) {
        Reservation reservation = reservationRepository.findById(reservationNo)
            .orElseThrow(() -> new RuntimeException("Reservation not found: " + reservationNo));
        return reservation.getGuestName();
    }
    
    /**
     * Get guest name by folio number
     */
    public String getGuestNameByFolio(String folioNo) {
        CheckIn checkIn = checkInRepository.findById(folioNo)
            .orElseThrow(() -> new RuntimeException("Check-in not found: " + folioNo));
        return checkIn.getGuestName();
    }

    private Advance createAdvanceEntity(AdvanceRequest request) {
        Advance advance = new Advance();
        advance.setReceiptNo(numberGenerationService.generateReceiptNumber());
        advance.setGuestName(request.getGuestName());
        advance.setDate(request.getDate() != null ? request.getDate() : LocalDate.now());
        advance.setArrivalDate(request.getArrivalDate());
        advance.setAuditDate(LocalDate.now());
        advance.setShiftDate(LocalDate.now());
        advance.setShiftNo("1"); // Default shift, should be configured
        advance.setModeOfPaymentId(request.getModeOfPaymentId());
        advance.setAmount(request.getAmount());
        advance.setCreditCardCompany(request.getCreditCardCompany());
        advance.setCardNumber(request.getCardNumber());
        advance.setOnlineCompanyName(request.getOnlineCompanyName());
        advance.setDetails(request.getDetails());
        advance.setNarration(request.getNarration());
        
        return advance;
    }
    
    private void validateAdvanceRequest(AdvanceRequest request) {
        if (!billSettlementTypeRepository.existsById(request.getModeOfPaymentId())) {
            throw new RuntimeException("Payment mode not found: " + request.getModeOfPaymentId());
        }
        
        if (request.getAmount() != null && request.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Amount cannot be negative");
        }
    }
    
    private AdvanceResponse mapToAdvanceResponse(Advance advance) {
        AdvanceResponse response = new AdvanceResponse();
        response.setReceiptNo(advance.getReceiptNo() != null ? advance.getReceiptNo() : "");
        response.setFolioNo(advance.getFolioNo() != null ? advance.getFolioNo() : "");
        response.setReservationNo(advance.getReservationNo() != null ? advance.getReservationNo() : "");
        response.setBillNo(advance.getBillNo() != null ? advance.getBillNo() : "");
        response.setGuestName(advance.getGuestName() != null ? advance.getGuestName() : "");
        response.setDate(advance.getDate());
        response.setArrivalDate(advance.getArrivalDate());
        response.setAuditDate(advance.getAuditDate());
        response.setShiftDate(advance.getShiftDate());
        response.setShiftNo(advance.getShiftNo() != null ? advance.getShiftNo() : "");
        response.setModeOfPaymentId(advance.getModeOfPaymentId() != null ? advance.getModeOfPaymentId() : "");
        response.setAmount(advance.getAmount() != null ? advance.getAmount() : BigDecimal.ZERO);
        response.setCreditCardCompany(advance.getCreditCardCompany() != null ? advance.getCreditCardCompany() : "");
        response.setCardNumber(advance.getCardNumber() != null ? advance.getCardNumber() : "");
        response.setOnlineCompanyName(advance.getOnlineCompanyName() != null ? advance.getOnlineCompanyName() : "");
        response.setDetails(advance.getDetails() != null ? advance.getDetails() : "");
        response.setNarration(advance.getNarration() != null ? advance.getNarration() : "");
        
        // Fetch and set payment mode name by ID
        if (advance.getModeOfPaymentId() != null) {
            billSettlementTypeRepository.findById(advance.getModeOfPaymentId())
                .ifPresent(paymentMode -> response.setModeOfPaymentName(paymentMode.getName() != null ? paymentMode.getName() : ""));
        } else {
            response.setModeOfPaymentName("");
        }
        
        // Fetch arrival date from reservation if not set and reservation exists
        if (advance.getArrivalDate() == null && advance.getReservationNo() != null) {
            reservationRepository.findById(advance.getReservationNo())
                .ifPresent(reservation -> response.setArrivalDate(reservation.getArrivalDate()));
        }
        
        return response;
    }
    
    private AdvanceSummaryResponse.AdvanceDetail mapToAdvanceDetail(Advance advance) {
        AdvanceSummaryResponse.AdvanceDetail detail = new AdvanceSummaryResponse.AdvanceDetail();
        detail.setReceiptNo(advance.getReceiptNo() != null ? advance.getReceiptNo() : "");
        detail.setDate(advance.getDate());
        detail.setAmount(advance.getAmount() != null ? advance.getAmount() : BigDecimal.ZERO);
        detail.setNarration(advance.getNarration() != null ? advance.getNarration() : "");
        
        // Fetch and set payment mode name by ID
        if (advance.getModeOfPaymentId() != null) {
            billSettlementTypeRepository.findById(advance.getModeOfPaymentId())
                .ifPresent(paymentMode -> detail.setModeOfPaymentName(paymentMode.getName() != null ? paymentMode.getName() : ""));
        } else {
            detail.setModeOfPaymentName("");
        }
        
        return detail;
    }

}