package com.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleCreateDto {
    @NotBlank(message = "Nom du rôle requis")
    @Size(max = 50)
    private String name;
    
    private String description;
}
