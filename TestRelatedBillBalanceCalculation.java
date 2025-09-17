import com.hotelworks.entity.FoBill;
import com.hotelworks.entity.CheckIn;
import com.hotelworks.entity.Reservation;
import java.math.BigDecimal;

public class TestRelatedBillBalanceCalculation {
    public static void main(String[] args) {
        System.out.println("Testing related bill balance calculation with rate...");
        
        // Create test data
        Reservation reservation = new Reservation();
        reservation.setRate(new BigDecimal("2000.00")); // Rate from reservation table
        
        CheckIn checkIn = new CheckIn();
        checkIn.setReservation(reservation);
        
        // Create original bill
        FoBill originalBill = new FoBill();
        originalBill.setCheckIn(checkIn);
        originalBill.setAdvanceAmount(new BigDecimal("500.00")); // Advances
        
        // Calculate balance using rate-based formula: Rate - Advances
        originalBill.calculateBalanceAmount();
        
        System.out.println("=== Original Bill ===");
        System.out.println("Rate from reservation: " + reservation.getRate());
        System.out.println("Advances: " + originalBill.getAdvanceAmount());
        System.out.println("Calculated Balance: " + originalBill.getBalanceAmount());
        
        // Create a split bill (related bill)
        FoBill splitBill = new FoBill();
        splitBill.setCheckIn(checkIn); // Same checkIn with reservation data
        splitBill.setAdvanceAmount(BigDecimal.ZERO); // No advances for split bill
        
        // Calculate balance for split bill using rate-based formula
        splitBill.calculateBalanceAmount();
        
        System.out.println("\n=== Split Bill ===");
        System.out.println("Rate from reservation: " + reservation.getRate());
        System.out.println("Advances: " + splitBill.getAdvanceAmount());
        System.out.println("Calculated Balance: " + splitBill.getBalanceAmount());
        
        // Verify both bills use the same rate for balance calculation
        BigDecimal expectedOriginalBalance = new BigDecimal("1500.00"); // 2000 - 500
        BigDecimal expectedSplitBalance = new BigDecimal("2000.00");    // 2000 - 0
        
        boolean originalCorrect = originalBill.getBalanceAmount().compareTo(expectedOriginalBalance) == 0;
        boolean splitCorrect = splitBill.getBalanceAmount().compareTo(expectedSplitBalance) == 0;
        
        System.out.println("\n=== Test Results ===");
        System.out.println("Original Bill Balance Test: " + (originalCorrect ? "PASS" : "FAIL"));
        System.out.println("Split Bill Balance Test: " + (splitCorrect ? "PASS" : "FAIL"));
        System.out.println("Expected Original Balance: " + expectedOriginalBalance);
        System.out.println("Actual Original Balance: " + originalBill.getBalanceAmount());
        System.out.println("Expected Split Balance: " + expectedSplitBalance);
        System.out.println("Actual Split Balance: " + splitBill.getBalanceAmount());
        
        if (originalCorrect && splitCorrect) {
            System.out.println("\nSUCCESS: Both related bills correctly use rate for balance calculation!");
        } else {
            System.out.println("\nFAILURE: Related bills are not correctly using rate for balance calculation.");
        }
    }
}