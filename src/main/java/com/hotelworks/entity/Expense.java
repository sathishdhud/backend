package com.hotelworks.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "expenses")
public class Expense {
    
    @Id
    @Column(name = "expense_id")
    private String expenseId;
    
    @NotBlank
    @Column(name = "voucher_no", nullable = false)
    private String voucherNo;
    
    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;
    
    @NotBlank
    @Column(name = "account_head_id", nullable = false)
    private String accountHeadId;
    
    @NotNull
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
    
    @Column(name = "narration")
    private String narration;
    
    @NotBlank
    @Column(name = "shift_no", nullable = false)
    private String shiftNo;
    
    @NotNull
    @Column(name = "shift_date", nullable = false)
    private LocalDate shiftDate;
    
    // Constructors
    public Expense() {}
    
    public Expense(String expenseId, String voucherNo, LocalDate date, String accountHeadId, 
                   BigDecimal amount, String narration, String shiftNo, LocalDate shiftDate) {
        this.expenseId = expenseId;
        this.voucherNo = voucherNo;
        this.date = date;
        this.accountHeadId = accountHeadId;
        this.amount = amount;
        this.narration = narration;
        this.shiftNo = shiftNo;
        this.shiftDate = shiftDate;
    }
    
    // Getters and Setters
    public String getExpenseId() { return expenseId; }
    public void setExpenseId(String expenseId) { this.expenseId = expenseId; }
    
    public String getVoucherNo() { return voucherNo; }
    public void setVoucherNo(String voucherNo) { this.voucherNo = voucherNo; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public String getAccountHeadId() { return accountHeadId; }
    public void setAccountHeadId(String accountHeadId) { this.accountHeadId = accountHeadId; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getNarration() { return narration; }
    public void setNarration(String narration) { this.narration = narration; }
    
    public String getShiftNo() { return shiftNo; }
    public void setShiftNo(String shiftNo) { this.shiftNo = shiftNo; }
    
    public LocalDate getShiftDate() { return shiftDate; }
    public void setShiftDate(LocalDate shiftDate) { this.shiftDate = shiftDate; }
}