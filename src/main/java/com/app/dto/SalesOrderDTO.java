package com.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderDTO {
    private Long id;
    private String orderNumber;
    private CustomerDto customer;
    private LocalDate orderDate;
    private LocalDate deliveryDate;
    private String status;
    private BigDecimal totalAmount;
    private BigDecimal discountPercentage;
    private BigDecimal taxPercentage;
    private BigDecimal finalAmount;
    private String paymentStatus;
    private String notes;
    private UserDto createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SalesOrderItemDTO> items;
    private List<PaymentDTO> payments;
    private BigDecimal totalPaid;
    private BigDecimal remainingAmount;
}
