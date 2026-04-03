package com.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateDTO {
    @NotNull(message = "Date de paiement requise")
    private LocalDate paymentDate;
    
    @NotNull(message = "Montant requis")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal amount;
    
    @NotNull(message = "Méthode de paiement requise")
    private String paymentMethod; // CASH, CARD, TRANSFER, CHECK
    
    private String reference;
    private String notes;
}
