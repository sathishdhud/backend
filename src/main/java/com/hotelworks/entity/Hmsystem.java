package com.hotelworks.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "hmsystem")
public class Hmsystem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "shift_date")
    private LocalDate shiftDate;
    
    @Column(name = "running_shift")
    private Integer runningShift;
    
    @Column(name = "total_shift")
    private Integer totalShift;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public Hmsystem() {}
    
    public Hmsystem(LocalDate shiftDate, Integer runningShift, Integer totalShift) {
        this.shiftDate = shiftDate;
        this.runningShift = runningShift;
        this.totalShift = totalShift;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public LocalDate getShiftDate() { return shiftDate; }
    public void setShiftDate(LocalDate shiftDate) { this.shiftDate = shiftDate; }
    
    public Integer getRunningShift() { return runningShift; }
    public void setRunningShift(Integer runningShift) { this.runningShift = runningShift; }
    
    public Integer getTotalShift() { return totalShift; }
    public void setTotalShift(Integer totalShift) { this.totalShift = totalShift; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}