package com.app.controllers;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import com.app.dto.CustomerCreateDto;
import com.app.dto.CustomerDto;
import com.app.dto.PageResponse;
import com.app.services.CustomerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {
    
    private final CustomerService customerService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CustomerDto>>> getAllCustomers(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) 
            Pageable pageable) {
        PageResponse<CustomerDto> customers = customerService.getAllCustomers(pageable);
        return ResponseEntity.ok(ApiResponse.success(customers));
    }
    
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<CustomerDto>>> getAllCustomersList() {
        List<CustomerDto> customers = customerService.getAllCustomersList();
        return ResponseEntity.ok(ApiResponse.success(customers));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<CustomerDto>>> searchCustomers(
            @RequestParam String query,
            @PageableDefault(size = 20) Pageable pageable) {
        PageResponse<CustomerDto> customers = customerService.searchCustomers(query, pageable);
        return ResponseEntity.ok(ApiResponse.success(customers));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerDto>> getCustomerById(@PathVariable Long id) {
        CustomerDto customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(ApiResponse.success(customer));
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<CustomerDto>> getCustomerByCode(@PathVariable String code) {
        CustomerDto customer = customerService.getCustomerByCode(code);
        return ResponseEntity.ok(ApiResponse.success(customer));
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES')")
    public ResponseEntity<ApiResponse<CustomerDto>> createCustomer(
            @Valid @RequestBody CustomerCreateDto dto) {
        CustomerDto customer = customerService.createCustomer(dto);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Client créé avec succès", customer));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES')")
    public ResponseEntity<ApiResponse<CustomerDto>> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerCreateDto dto) {
        CustomerDto customer = customerService.updateCustomer(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Client mis à jour", customer));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(ApiResponse.success("Client supprimé", null));
    }
}
