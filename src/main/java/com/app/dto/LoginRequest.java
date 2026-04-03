package com.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Username est requis")
    private String username;
    
    @NotBlank(message = "Password est requis")
    private String password;
}
