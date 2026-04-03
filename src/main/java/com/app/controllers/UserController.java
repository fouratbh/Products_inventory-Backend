package com.app.controllers;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.app.dto.ApiResponse;
import com.app.dto.PageResponse;
import com.app.dto.PasswordChangeDto;
import com.app.dto.UserCreateDto;
import com.app.dto.UserDto;
import com.app.dto.UserUpdateDto;
import com.app.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    

    
    @SuppressWarnings("unchecked")
	@GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<PageResponse<UserDto>>> getAllUsers(
    		@PageableDefault(size = 20, sort ="username", direction = Sort.Direction.ASC)
    		Pageable pageable) {
    	
    		PageResponse<UserDto> users = (PageResponse<UserDto>) userService.getAllUsers(pageable);
    		return ResponseEntity.ok(ApiResponse.success(users));
    		
    		
    }
    
        
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser() {
    	UserDto user = userService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> createUser(@Valid @RequestBody UserCreateDto dto) {
    	UserDto user = userService.createUser(dto);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Utilisateur créé avec succès", user));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDto dto) {
    	UserDto user = userService.updateUser(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Utilisateur mis à jour", user));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("Utilisateur supprimé", null));
    }
    
    @PutMapping("/{id}/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody PasswordChangeDto dto) {
        userService.changePassword(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Mot de passe modifié", null));
    }
}