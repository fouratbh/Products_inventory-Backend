package com.app.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private Long totalProducts;
    private Long lowStockProducts;
    private Long outOfStockProducts;
    private BigDecimal totalStockValue;
    
    private Long totalCustomers;
    private Long totalSuppliers;
    
    private Long totalPurchaseOrders;
    private Long pendingPurchaseOrders;
    private BigDecimal monthlyPurchaseAmount;
    
    private Long totalSalesOrders;
    private Long pendingSalesOrders;
    private BigDecimal monthlySalesAmount;
    
    private BigDecimal totalOutstanding;
    private Long unpaidOrders;
    
    private List<CategoryStatsDTO> topCategories;
    private List<ProductStatsDTO> topSellingProducts;
    private List<MonthlyRevenueDTO> monthlyRevenue;
}


