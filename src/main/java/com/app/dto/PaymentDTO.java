package com.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.*;


public record PaymentDTO (Long id, Long salesOrderId, LocalDate paymentDate, BigDecimal amount, String paymentMethod, String reference, String notes, UserDto createdBy, LocalDateTime createdAt) {
   
}
