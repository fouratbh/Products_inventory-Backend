package com.app.dto;

import java.time.LocalDateTime;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementDTO {
    private Long id;
    private ProductDto product;
    private String movementType;
    private Integer quantity;
    private String referenceType;
    private Long referenceId;
    private String notes;
    private UserDto createdBy;
    private LocalDateTime createdAt;
}
