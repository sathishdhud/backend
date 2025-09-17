import com.hotelworks.entity.FoBill;
import java.math.BigDecimal;

public class TestOverpaymentValues {
    public static void main(String[] args) {
        System.out.println("Testing overpayment values fix...");
        
        // Simulate the scenario from the API response
        FoBill bill = new FoBill();
        bill.setTotalAmount(new BigDecimal("1000.00"));
        bill.setPaidAmount(new BigDecimal("1800.00")); // Full advance amount
        
        System.out.println("Scenario: Total Amount: 1000.00, Advance Amount: 1800.00");
        System.out.println("Total Amount: " + bill.getTotalAmount());
        System.out.println("Paid Amount: " + bill.getPaidAmount());
        System.out.println("Balance Amount: " + bill.getBalanceAmount());
        System.out.println("Settlement Status: " + bill.getSettlementStatus());
        
        // Expected results:
        // Total Amount: 1000.00
        // Paid Amount: 1800.00 (full advance amount)
        // Balance Amount: 0.00 (not negative)
        // Settlement Status: SETTLED
        
        boolean totalCorrect = bill.getTotalAmount().compareTo(new BigDecimal("1000.00")) == 0;
        boolean paidCorrect = bill.getPaidAmount().compareTo(new BigDecimal("1800.00")) == 0;
        boolean balanceCorrect = bill.getBalanceAmount().compareTo(BigDecimal.ZERO) == 0;
        boolean statusCorrect = "SETTLED".equals(bill.getSettlementStatus());
        
        System.out.println("\nValidation Results:");
        System.out.println("Total Amount Correct: " + totalCorrect);
        System.out.println("Paid Amount Correct: " + paidCorrect);
        System.out.println("Balance Amount Correct: " + balanceCorrect);
        System.out.println("Settlement Status Correct: " + statusCorrect);
        
        if (totalCorrect && paidCorrect && balanceCorrect && statusCorrect) {
            System.out.println("\nSUCCESS: All values are correctly calculated!");
        } else {
            System.out.println("\nFAILURE: Some values are incorrect.");
        }
    }
}