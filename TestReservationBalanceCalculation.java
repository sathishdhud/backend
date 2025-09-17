import com.hotelworks.entity.FoBill;
import com.hotelworks.entity.CheckIn;
import com.hotelworks.entity.Reservation;
import java.math.BigDecimal;

public class TestReservationBalanceCalculation {
    public static void main(String[] args) {
        System.out.println("Testing reservation-based balance calculation...");
        
        // Create test data
        Reservation reservation = new Reservation();
        reservation.setRate(new BigDecimal("1500.00")); // Rate from reservation table
        
        CheckIn checkIn = new CheckIn();
        checkIn.setReservation(reservation);
        
        FoBill bill = new FoBill();
        bill.setCheckIn(checkIn);
        bill.setAdvanceAmount(new BigDecimal("800.00")); // Advances
        
        // Calculate balance using new formula: Rate - Advances
        bill.calculateBalanceAmount();
        
        System.out.println("Rate from reservation: " + reservation.getRate());
        System.out.println("Advances: " + bill.getAdvanceAmount());
        System.out.println("Calculated Balance: " + bill.getBalanceAmount());
        
        // Expected result:
        // Rate: 1500.00
        // Advances: 800.00
        // Balance: 700.00 (1500 - 800)
        
        BigDecimal expectedBalance = new BigDecimal("700.00");
        boolean correct = bill.getBalanceAmount().compareTo(expectedBalance) == 0;
        
        System.out.println("\nTest Result: " + (correct ? "PASS" : "FAIL"));
        System.out.println("Expected Balance: " + expectedBalance);
        System.out.println("Actual Balance: " + bill.getBalanceAmount());
        
        // Test overpayment scenario
        System.out.println("\n--- Overpayment Test ---");
        bill.setAdvanceAmount(new BigDecimal("2000.00")); // More than rate
        bill.calculateBalanceAmount();
        
        System.out.println("Rate from reservation: " + reservation.getRate());
        System.out.println("Advances: " + bill.getAdvanceAmount());
        System.out.println("Calculated Balance: " + bill.getBalanceAmount());
        
        // Expected result:
        // Rate: 1500.00
        // Advances: 2000.00
        // Balance: 0.00 (not negative)
        
        boolean overpaymentCorrect = bill.getBalanceAmount().compareTo(BigDecimal.ZERO) == 0;
        System.out.println("\nOverpayment Test Result: " + (overpaymentCorrect ? "PASS" : "FAIL"));
        System.out.println("Expected Balance: 0.00");
        System.out.println("Actual Balance: " + bill.getBalanceAmount());
    }
}