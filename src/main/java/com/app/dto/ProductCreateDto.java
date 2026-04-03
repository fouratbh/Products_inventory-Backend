package com.app.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateDto {
    @NotBlank(message = "Code produit requis")
    @Size(max = 50)
    private String code;
    
    @NotBlank(message = "Nom produit requis")
    @Size(max = 200)
    private String name;
    
    private String description;
    
    @NotNull(message = "Catégorie requise")
    private Long categoryId;
    
    @NotNull(message = "Fournisseur requis")
    private Long supplierId;
    
    @NotNull(message = "Prix d'achat requis")
    @DecimalMin(value = "0.0", inclusive = false, message = "Prix d'achat doit être positif")
    private BigDecimal purchasePrice;
    
    @NotNull(message = "Prix de vente requis")
    @DecimalMin(value = "0.0", inclusive = false, message = "Prix de vente doit être positif")
    private BigDecimal sellingPrice;
    
    @Min(value = 0, message = "Quantité ne peut pas être négative")
    private Integer quantityInStock;
    
    private Integer minStockLevel;
    private Integer maxStockLevel;
    private String unit;
    private Integer warrantyMonths;
}
