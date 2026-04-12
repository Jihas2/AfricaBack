package com.romeogolf.residence.sale;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findByBuyerId(Long buyerId);
    long countByStatus(SaleStatus status);

    @Query("SELECT COALESCE(SUM(s.paidAmount), 0) FROM Sale s")
    BigDecimal sumPaidAmount();
}
