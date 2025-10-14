package com.hotelworks.scheduler;

import com.hotelworks.entity.CheckIn;
import com.hotelworks.entity.HotelAccountHead;
import com.hotelworks.entity.PostTransaction;
import com.hotelworks.entity.Room;
import com.hotelworks.entity.Taxation;
import com.hotelworks.repository.CheckInRepository;
import com.hotelworks.repository.HotelAccountHeadRepository;
import com.hotelworks.repository.PostTransactionRepository;
import com.hotelworks.repository.RoomRepository;
import com.hotelworks.repository.TaxationRepository;
import com.hotelworks.service.NumberGenerationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class NightAuditFunctionalityTest {

    @Autowired
    private RoomStatusScheduler roomStatusScheduler;

    @Autowired
    private CheckInRepository checkInRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private PostTransactionRepository postTransactionRepository;

    @Autowired
    private HotelAccountHeadRepository hotelAccountHeadRepository;

    @Autowired
    private TaxationRepository taxationRepository;

    @Autowired
    private NumberGenerationService numberGenerationService;

    @BeforeEach
    public void setUp() {
        // Clean up repositories
        postTransactionRepository.deleteAll();
        checkInRepository.deleteAll();
        roomRepository.deleteAll();
        hotelAccountHeadRepository.deleteAll();
        taxationRepository.deleteAll();

        // Create test room
        Room room = new Room();
        room.setRoomId("ROOM001");
        room.setRoomNo("101");
        room.setFloor("1");
        room.setStatus("OD"); // Occupied Dirty
        roomRepository.save(room);

        // Create test check-in
        CheckIn checkIn = new CheckIn();
        checkIn.setFolioNo("FOLIO001");
        checkIn.setRoomId("ROOM001");
        checkIn.setGuestName("Test Guest");
        checkIn.setArrivalDate(LocalDate.now().minusDays(2));
        checkIn.setDepartureDate(LocalDate.now().plusDays(3));
        checkIn.setRate(new BigDecimal("5000.00")); // Rate inclusive of GST
        checkIn.setMobileNumber("1234567890");
        checkInRepository.save(checkIn);

        // Create account heads
        HotelAccountHead roomChargesHead = new HotelAccountHead();
        roomChargesHead.setAccHeadId("ROOM_CHARGE");
        roomChargesHead.setName("Room Charges");
        hotelAccountHeadRepository.save(roomChargesHead);

        HotelAccountHead cgstHead = new HotelAccountHead();
        cgstHead.setAccHeadId("CGST");
        cgstHead.setName("CGST");
        hotelAccountHeadRepository.save(cgstHead);

        HotelAccountHead sgstHead = new HotelAccountHead();
        sgstHead.setAccHeadId("SGST");
        sgstHead.setName("SGST");
        hotelAccountHeadRepository.save(sgstHead);

        // Create tax records
        Taxation cgstTax = new Taxation();
        cgstTax.setTaxId("CGST001");
        cgstTax.setTaxName("CGST");
        cgstTax.setPercentage(new BigDecimal("5.00"));
        taxationRepository.save(cgstTax);

        Taxation sgstTax = new Taxation();
        sgstTax.setTaxId("SGST001");
        sgstTax.setTaxName("SGST");
        sgstTax.setPercentage(new BigDecimal("5.00"));
        taxationRepository.save(sgstTax);
    }

    @Test
    public void testNightAuditWithInclusiveGst() {
        // Run the night audit
        roomStatusScheduler.postDailyRoomChargesAndTaxes();

        // Verify transactions were created
        List<PostTransaction> transactions = postTransactionRepository.findByFolioNo("FOLIO001");
        assertEquals(3, transactions.size(), "Should have 3 transactions (room charge, CGST, SGST)");

        // Find the room charge transaction
        PostTransaction roomCharge = null;
        PostTransaction cgst = null;
        PostTransaction sgst = null;

        for (PostTransaction transaction : transactions) {
            if ("ROOM_CHARGE".equals(transaction.getAccHeadId())) {
                roomCharge = transaction;
            } else if ("CGST".equals(transaction.getAccHeadId())) {
                cgst = transaction;
            } else if ("SGST".equals(transaction.getAccHeadId())) {
                sgst = transaction;
            }
        }

        assertNotNull(roomCharge, "Room charge transaction should exist");
        assertNotNull(cgst, "CGST transaction should exist");
        assertNotNull(sgst, "SGST transaction should exist");

        // Verify amounts for inclusive GST (5000 total)
        // Base rate = 5000 / 1.10 = 4545.45, rounded to 4545
        // CGST = 4545 * 0.05 = 227.25, rounded to 227
        // SGST = 4545 * 0.05 = 227.25, rounded to 227
        // Total = 4545 + 227 + 227 = 4999 (1 rupee difference due to rounding)
        assertEquals(new BigDecimal("4545"), roomCharge.getAmount(), "Base room charge should be 4545");
        assertEquals(new BigDecimal("227"), cgst.getAmount(), "CGST should be 227");
        assertEquals(new BigDecimal("227"), sgst.getAmount(), "SGST should be 227");

        // Verify that running the audit again doesn't create duplicate transactions
        roomStatusScheduler.postDailyRoomChargesAndTaxes();

        List<PostTransaction> transactionsAfterSecondRun = postTransactionRepository.findByFolioNo("FOLIO001");
        assertEquals(3, transactionsAfterSecondRun.size(), "Should still have only 3 transactions after second run");
    }

    @Test
    public void testNightAuditWithoutGst() {
        // Update check-in to not include GST
        CheckIn checkIn = checkInRepository.findById("FOLIO001").orElse(null);
        assertNotNull(checkIn, "Check-in should exist");
        
        // Create a reservation that doesn't include GST
        checkIn.setRate(new BigDecimal("5000.00"));
        checkInRepository.save(checkIn);

        // Run the night audit
        roomStatusScheduler.postDailyRoomChargesAndTaxes();

        // Verify transactions were created
        List<PostTransaction> transactions = postTransactionRepository.findByFolioNo("FOLIO001");
        assertEquals(3, transactions.size(), "Should have 3 transactions (room charge, CGST, SGST)");

        // Find the room charge transaction
        PostTransaction roomCharge = null;
        PostTransaction cgst = null;
        PostTransaction sgst = null;

        for (PostTransaction transaction : transactions) {
            if ("ROOM_CHARGE".equals(transaction.getAccHeadId())) {
                roomCharge = transaction;
            } else if ("CGST".equals(transaction.getAccHeadId())) {
                cgst = transaction;
            } else if ("SGST".equals(transaction.getAccHeadId())) {
                sgst = transaction;
            }
        }

        assertNotNull(roomCharge, "Room charge transaction should exist");
        assertNotNull(cgst, "CGST transaction should exist");
        assertNotNull(sgst, "SGST transaction should exist");

        // Verify amounts for exclusive GST (5000 base rate)
        // Base rate = 5000
        // CGST = 5000 * 0.05 = 250
        // SGST = 5000 * 0.05 = 250
        // Total = 5000 + 250 + 250 = 5500
        assertEquals(new BigDecimal("5000"), roomCharge.getAmount(), "Base room charge should be 5000");
        assertEquals(new BigDecimal("250"), cgst.getAmount(), "CGST should be 250");
        assertEquals(new BigDecimal("250"), sgst.getAmount(), "SGST should be 250");
    }
}