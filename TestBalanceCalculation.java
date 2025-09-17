import com.hotelworks.entity.FoBill;
import java.math.BigDecimal;

public class TestBalanceCalculation {
    public static void main(String[] args) {
        // Test case from the issue
        FoBill bill = new FoBill();
        bill.setTotalAmount(new BigDecimal("100.00"));
        bill.setPaidAmount(new BigDecimal("500.00")); // Advance paid
        
        System.out.println("Before fix:");
        System.out.println("Total Amount: " + bill.getTotalAmount());
        System.out.println("Paid Amount: " + bill.getPaidAmount());
        
        // Old calculation would give -400.00
        bill.calculateBalanceAmount();
        System.out.println("Balance Amount: " + bill.getBalanceAmount());
        
        bill.updateSettlementStatus();
        System.out.println("Settlement Status: " + bill.getSettlementStatus());
        
        // Expected output after fix:
        // Total Amount: 100.00
        // Paid Amount: 500.00
        // Balance Amount: 0.00 (not -400.00)
        // Settlement Status: SETTLED
    }
}