package com.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderCreateDTO {
    @NotNull(message = "Client requis")
    private Long customerId;
    
    @NotNull(message = "Date de commande requise")
    private LocalDate orderDate;
    
    private LocalDate deliveryDate;
    
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private BigDecimal discountPercentage;
    
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private BigDecimal taxPercentage;
    
    private String notes;
    
    @NotEmpty(message = "Au moins un article requis")
    @Valid
    private List<SalesOrderItemCreateDTO> items;
}
