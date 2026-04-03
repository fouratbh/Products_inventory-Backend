package com.app.dto;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Username est requis")
    @Size(min = 3, max = 50, message = "Username doit avoir entre 3 et 50 caractères")
    private String username;
    
    @NotBlank(message = "Email est requis")
    @Email(message = "Email invalide")
    private String email;
    
    @NotBlank(message = "Password est requis")
    @Size(min = 6, message = "Password doit avoir au moins 6 caractères")
    private String password;
    
    private String firstName;
    private String lastName;
    
    @NotEmpty(message = "Au moins un rôle est requis")
    private Set<String> roles;
}
