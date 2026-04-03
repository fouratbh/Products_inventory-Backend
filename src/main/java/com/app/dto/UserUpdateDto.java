package com.app.dto;

import java.util.Set;

import jakarta.validation.constraints.Email;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {
	
	@Email(message = "Email invalide")
    private String email;
    
    private String firstName;
    private String lastName;
    private Boolean enabled;
    private Set<Long> roleIds;
}

