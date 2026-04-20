package com.app.controllers;

import com.app.dto.ApiResponse;
import com.app.dto.UserDto;
import com.app.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth") // On change le path pour suivre les standards
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Cet endpoint permet au Front-end de récupérer les détails de l'utilisateur 
     * à partir du token JWT envoyé dans le header.
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser() {
        UserDto user = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success("Profil récupéré", user));
    }
}
    
