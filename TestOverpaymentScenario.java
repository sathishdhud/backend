import com.hotelworks.entity.FoBill;
import java.math.BigDecimal;

public class TestOverpaymentScenario {
    public static void main(String[] args) {
        System.out.println("Testing overpayment scenario...");
        
        // Create a bill with total amount of 100
        FoBill bill = new FoBill();
        bill.setTotalAmount(new BigDecimal("100.00"));
        
        // Simulate an overpayment of 500
        bill.setPaidAmount(new BigDecimal("500.00"));
        
        System.out.println("Before calculation:");
        System.out.println("Total Amount: " + bill.getTotalAmount());
        System.out.println("Paid Amount: " + bill.getPaidAmount());
        
        // Calculate balance
        bill.calculateBalanceAmount();
        System.out.println("After calculation:");
        System.out.println("Balance Amount: " + bill.getBalanceAmount());
        
        // Update settlement status
        bill.updateSettlementStatus();
        System.out.println("Settlement Status: " + bill.getSettlementStatus());
        
        // Expected output:
        // Total Amount: 100.00
        // Paid Amount: 500.00
        // Balance Amount: 0.00 (not -400.00)
        // Settlement Status: SETTLED
        
        if (bill.getBalanceAmount().compareTo(BigDecimal.ZERO) == 0) {
            System.out.println("SUCCESS: Balance correctly set to zero for overpayment");
        } else {
            System.out.println("FAILURE: Balance is not zero: " + bill.getBalanceAmount());
        }
        
        if ("SETTLED".equals(bill.getSettlementStatus())) {
            System.out.println("SUCCESS: Bill correctly marked as SETTLED");
        } else {
            System.out.println("FAILURE: Bill not marked as SETTLED, status is: " + bill.getSettlementStatus());
        }
    }
}