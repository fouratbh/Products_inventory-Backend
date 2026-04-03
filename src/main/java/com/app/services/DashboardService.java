package com.app.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dto.DashboardStatsDTO;
import com.app.dto.MonthlyRevenueDTO;
import com.app.repos.CustomerRepository;
import com.app.repos.ProductRepository;
import com.app.repos.PurchaseOrderRepository;
import com.app.repos.SalesOrderRepository;
import com.app.repos.SupplierRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardService {
    
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final SupplierRepository supplierRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SalesOrderRepository salesOrderRepository;
    
    public DashboardStatsDTO getDashboardStats() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        
        return DashboardStatsDTO.builder()
            // Product stats
            .totalProducts(productRepository.count())
            .lowStockProducts((long) productRepository.findLowStockProducts().size())
            .outOfStockProducts((long) productRepository.findOutOfStockProducts().size())
            .totalStockValue(productRepository.calculateTotalStockValue())
            
            // Customer and Supplier stats
            .totalCustomers(customerRepository.count())
            .totalSuppliers(supplierRepository.count())
            
            // Purchase stats
            .totalPurchaseOrders(purchaseOrderRepository.count())
            .pendingPurchaseOrders(purchaseOrderRepository
                .findByStatus(com.app.entities.PurchaseOrder.OrderStatus.PENDING, 
                    org.springframework.data.domain.Pageable.unpaged())
                .getTotalElements())
            .monthlyPurchaseAmount(purchaseOrderRepository
                .calculateTotalPurchaseAmount(startOfMonth, endOfMonth))
            
            // Sales stats
            .totalSalesOrders(salesOrderRepository.count())
            .pendingSalesOrders(salesOrderRepository
                .findByStatus(com.app.entities.SalesOrder.OrderStatus.DRAFT, 
                    org.springframework.data.domain.Pageable.unpaged())
                .getTotalElements())
            .monthlySalesAmount(salesOrderRepository
                .calculateTotalSalesAmount(startOfMonth, endOfMonth))
            
            // Payment stats
            .totalOutstanding(salesOrderRepository.calculateTotalOutstanding())
            .unpaidOrders(salesOrderRepository
                .findByPaymentStatus(com.app.entities.SalesOrder.PaymentStatus.PENDING,
                    org.springframework.data.domain.Pageable.unpaged())
                .getTotalElements())
            
            // Monthly revenue for last 6 months
            .monthlyRevenue(getMonthlyRevenue(6))
            
            .build();
    }
    
    private List<MonthlyRevenueDTO> getMonthlyRevenue(int numberOfMonths) {
        List<MonthlyRevenueDTO> result = new ArrayList<>();
        
        IntStream.range(0, numberOfMonths)
            .mapToObj(i -> YearMonth.now().minusMonths(numberOfMonths - 1 - i))
            .forEach(yearMonth -> {
                LocalDate startDate = yearMonth.atDay(1);
                LocalDate endDate = yearMonth.atEndOfMonth();
                
                BigDecimal revenue = salesOrderRepository
                    .calculateTotalSalesAmount(startDate, endDate);
                BigDecimal purchases = purchaseOrderRepository
                    .calculateTotalPurchaseAmount(startDate, endDate);
                
                revenue = revenue != null ? revenue : BigDecimal.ZERO;
                purchases = purchases != null ? purchases : BigDecimal.ZERO;
                BigDecimal profit = revenue.subtract(purchases);
                
                result.add(MonthlyRevenueDTO.builder()
                    .month(yearMonth.toString())
                    .revenue(revenue)
                    .purchases(purchases)
                    .profit(profit)
                    .build());
            });
        
        return result;
    }
}