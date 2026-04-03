package com.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeDto {
    @NotBlank(message = "Ancien password requis")
    private String oldPassword;
    
    @NotBlank(message = "Nouveau password requis")
    @Size(min = 6)
    private String newPassword;
}
