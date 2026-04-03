package com.app.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderCreateDTO {
    @NotNull(message = "Fournisseur requis")
    private Long supplierId;
    
    @NotNull(message = "Date de commande requise")
    private LocalDate orderDate;
    
    private LocalDate deliveryDate;
    private String notes;
    
    @NotEmpty(message = "Au moins un article requis")
    @Valid
    private List<PurchaseOrderItemCreateDTO> items;
}
