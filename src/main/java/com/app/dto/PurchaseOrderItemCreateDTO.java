package com.app.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderItemCreateDTO {
    @NotNull(message = "Produit requis")
    private Long productId;
    
    @NotNull(message = "Quantité requise")
    @Min(value = 1, message = "Quantité doit être au moins 1")
    private Integer quantity;
    
    @NotNull(message = "Prix unitaire requis")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal unitPrice;
}
