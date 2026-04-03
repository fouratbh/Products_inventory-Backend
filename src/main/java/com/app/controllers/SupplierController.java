package com.app.controllers;

import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Sort;


import com.app.dto.ApiResponse;
import com.app.dto.PageResponse;
import com.app.dto.SupplierCreateDto;
import com.app.dto.SupplierDto;
import com.app.services.SupplierService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/suppliers")
@RequiredArgsConstructor
public class SupplierController {
    
    private final SupplierService supplierService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<SupplierDto>>> getAllSuppliers(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) 
            Pageable pageable) {
        PageResponse<SupplierDto> suppliers = supplierService.getAllSuppliers(pageable);
        return ResponseEntity.ok(ApiResponse.success(suppliers));
    }
    
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<java.util.List<SupplierDto>>> getAllSuppliersList() {
        java.util.List<SupplierDto> suppliers = supplierService.getAllSuppliersList();
        return ResponseEntity.ok(ApiResponse.success(suppliers));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<SupplierDto>>> searchSuppliers(
            @RequestParam String query,
            @PageableDefault(size = 20) Pageable pageable) {
        PageResponse<SupplierDto> suppliers = supplierService.searchSuppliers(query, pageable);
        return ResponseEntity.ok(ApiResponse.success(suppliers));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierDto>> getSupplierById(@PathVariable Long id) {
    	SupplierDto supplier = supplierService.getSupplierById(id);
        return ResponseEntity.ok(ApiResponse.success(supplier));
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<SupplierDto>> createSupplier(
            @Valid @RequestBody SupplierCreateDto dto) {
    	SupplierDto supplier = supplierService.createSupplier(dto);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Fournisseur créé avec succès", supplier));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<SupplierDto>> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody SupplierCreateDto dto) {
        SupplierDto supplier = supplierService.updateSupplier(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Fournisseur mis à jour", supplier));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.ok(ApiResponse.success("Fournisseur supprimé", null));
    }
}
