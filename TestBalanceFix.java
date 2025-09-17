import com.hotelworks.entity.FoBill;
import java.math.BigDecimal;

public class TestBalanceFix {
    public static void main(String[] args) {
        System.out.println("Testing balance calculation fix...");
        
        // Test case 1: Normal case
        FoBill bill1 = new FoBill();
        bill1.setTotalAmount(new BigDecimal("100.00"));
        bill1.setPaidAmount(new BigDecimal("30.00"));
        
        System.out.println("Test 1 - Normal case:");
        System.out.println("Total Amount: " + bill1.getTotalAmount());
        System.out.println("Paid Amount: " + bill1.getPaidAmount());
        System.out.println("Balance Amount: " + bill1.getBalanceAmount());
        System.out.println("Settlement Status: " + bill1.getSettlementStatus());
        System.out.println();
        
        // Test case 2: Overpayment case
        FoBill bill2 = new FoBill();
        bill2.setTotalAmount(new BigDecimal("100.00"));
        bill2.setPaidAmount(new BigDecimal("150.00"));
        
        System.out.println("Test 2 - Overpayment case:");
        System.out.println("Total Amount: " + bill2.getTotalAmount());
        System.out.println("Paid Amount: " + bill2.getPaidAmount());
        System.out.println("Balance Amount: " + bill2.getBalanceAmount());
        System.out.println("Settlement Status: " + bill2.getSettlementStatus());
        System.out.println();
        
        // Test case 3: Zero balance case
        FoBill bill3 = new FoBill();
        bill3.setTotalAmount(new BigDecimal("100.00"));
        bill3.setPaidAmount(new BigDecimal("100.00"));
        
        System.out.println("Test 3 - Zero balance case:");
        System.out.println("Total Amount: " + bill3.getTotalAmount());
        System.out.println("Paid Amount: " + bill3.getPaidAmount());
        System.out.println("Balance Amount: " + bill3.getBalanceAmount());
        System.out.println("Settlement Status: " + bill3.getSettlementStatus());
        System.out.println();
        
        // Test case 4: Setting values in different order
        FoBill bill4 = new FoBill();
        bill4.setPaidAmount(new BigDecimal("200.00"));
        bill4.setTotalAmount(new BigDecimal("50.00"));
        
        System.out.println("Test 4 - Setting values in different order:");
        System.out.println("Total Amount: " + bill4.getTotalAmount());
        System.out.println("Paid Amount: " + bill4.getPaidAmount());
        System.out.println("Balance Amount: " + bill4.getBalanceAmount());
        System.out.println("Settlement Status: " + bill4.getSettlementStatus());
        System.out.println();
        
        // Verify all balances are correct
        boolean test1Pass = bill1.getBalanceAmount().compareTo(new BigDecimal("70.00")) == 0;
        boolean test2Pass = bill2.getBalanceAmount().compareTo(BigDecimal.ZERO) == 0;
        boolean test3Pass = bill3.getBalanceAmount().compareTo(BigDecimal.ZERO) == 0;
        boolean test4Pass = bill4.getBalanceAmount().compareTo(BigDecimal.ZERO) == 0;
        
        System.out.println("Test Results:");
        System.out.println("Test 1 (Normal case): " + (test1Pass ? "PASS" : "FAIL"));
        System.out.println("Test 2 (Overpayment): " + (test2Pass ? "PASS" : "FAIL"));
        System.out.println("Test 3 (Zero balance): " + (test3Pass ? "PASS" : "FAIL"));
        System.out.println("Test 4 (Order independence): " + (test4Pass ? "PASS" : "FAIL"));
        
        if (test1Pass && test2Pass && test3Pass && test4Pass) {
            System.out.println("\nAll tests PASSED! Balance calculation fix is working correctly.");
        } else {
            System.out.println("\nSome tests FAILED! There may still be issues with the balance calculation.");
        }
    }
}