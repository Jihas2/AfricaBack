package com.romeogolf.residence.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DashboardStats {
    private BigDecimal totalRevenue;
    private long       totalSales;
    private long       completedSales;
    private long       pendingPayments;
    private long       availableUnits;
    private long       totalBuyers;
}
