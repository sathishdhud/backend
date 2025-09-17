import com.hotelworks.dto.response.BillResponse;
import java.math.BigDecimal;

public class TestBillResponseFields {
    public static void main(String[] args) {
        BillResponse response = new BillResponse();
        
        // Set both fields to demonstrate the difference
        response.setTotalAmount(new BigDecimal("1000.00")); // Total bill amount
        response.setTotalTransactionAmount(new BigDecimal("1000.00")); // Sum of transactions
        
        System.out.println("Bill Response Fields:");
        System.out.println("Total Amount: " + response.getTotalAmount());
        System.out.println("Total Transaction Amount: " + response.getTotalTransactionAmount());
        
        System.out.println("\nBoth fields are now available in the response:");
        System.out.println("- totalAmount: Represents the total bill amount");
        System.out.println("- totalTransactionAmount: Represents the sum of all transaction amounts");
    }
}