package com.app.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiveProductDTO {
    @NotNull(message = "Quantité requise")
    @Min(value = 1)
    private Integer quantity;
    
    private String notes;
}
