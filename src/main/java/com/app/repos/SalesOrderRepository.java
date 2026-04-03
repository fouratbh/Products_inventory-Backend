package com.app.repos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.entities.SalesOrder;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {
    Optional<SalesOrder> findByOrderNumber(String orderNumber);
    Boolean existsByOrderNumber(String orderNumber);
    
    Page<SalesOrder> findByStatus(SalesOrder.OrderStatus status, Pageable pageable);
    Page<SalesOrder> findByCustomerId(Long customerId, Pageable pageable);
    Page<SalesOrder> findByPaymentStatus(SalesOrder.PaymentStatus paymentStatus, Pageable pageable);
    
    @Query("SELECT so FROM SalesOrder so WHERE so.orderDate BETWEEN :startDate AND :endDate")
    List<SalesOrder> findByDateRange(@Param("startDate") LocalDate startDate, 
                                     @Param("endDate") LocalDate endDate);
    
    @Query("SELECT so FROM SalesOrder so LEFT JOIN FETCH so.items LEFT JOIN FETCH so.payments WHERE so.id = :id")
    Optional<SalesOrder> findByIdWithDetails(@Param("id") Long id);
    
    @Query("SELECT SUM(so.finalAmount) FROM SalesOrder so WHERE " +
           "so.status != 'CANCELLED' AND so.orderDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalSalesAmount(@Param("startDate") LocalDate startDate, 
                                        @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(so.finalAmount - (SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.salesOrder = so)) " +
           "FROM SalesOrder so WHERE so.paymentStatus != 'PAID' AND so.status != 'CANCELLED'")
    BigDecimal calculateTotalOutstanding();
}
