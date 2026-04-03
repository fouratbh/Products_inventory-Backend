package com.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderUpdateDTO {
    private LocalDate deliveryDate;
    private String status;
    
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private BigDecimal discountPercentage;
    
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private BigDecimal taxPercentage;
    
    private String notes;
}

