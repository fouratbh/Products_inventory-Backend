package com.app.dto;

import java.time.LocalDate;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderUpdateDTO {
    private LocalDate deliveryDate;
    private String status;
    private String notes;
}
