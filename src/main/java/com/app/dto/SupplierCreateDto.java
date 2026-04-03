package com.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierCreateDto {
    @NotBlank(message = "Nom du fournisseur requis")
    @Size(max = 100)
    private String name;
    
    private String contactPerson;
    
    @Email(message = "Email invalide")
    private String email;
    
    private String phone;
    private String address;
    private String city;
    private String country;
}
