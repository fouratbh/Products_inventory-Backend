package com.app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductStockAdjustmentDto {
    @NotNull(message = "Quantité requise")
    private Integer quantity;
    
    private String notes;
}
