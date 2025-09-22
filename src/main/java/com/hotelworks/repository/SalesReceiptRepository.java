package com.hotelworks.repository;

import com.hotelworks.entity.SalesReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesReceiptRepository extends JpaRepository<SalesReceipt, String> {
}