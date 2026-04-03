package com.app.controllers;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Sort;


import com.app.dto.ApiResponse;
import com.app.dto.PageResponse;
import com.app.dto.PaymentCreateDTO;
import com.app.dto.PaymentDTO;
import com.app.dto.SalesOrderCreateDTO;
import com.app.dto.SalesOrderDTO;
import com.app.services.PaymentService;
import com.app.services.SalesOrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/sales-orders")
@RequiredArgsConstructor
public class SalesOrderController {
    
    private final SalesOrderService salesOrderService;
    private final PaymentService paymentService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<SalesOrderDTO>>> getAllSalesOrders(
            @PageableDefault(size = 20, sort = "orderDate", direction = Sort.Direction.DESC) 
            Pageable pageable) {
        PageResponse<SalesOrderDTO> orders = salesOrderService.getAllSalesOrders(pageable);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SalesOrderDTO>> getSalesOrderById(@PathVariable Long id) {
        SalesOrderDTO order = salesOrderService.getSalesOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(order));
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES')")
    public ResponseEntity<ApiResponse<SalesOrderDTO>> createSalesOrder(
            @Valid @RequestBody SalesOrderCreateDTO dto) {
        SalesOrderDTO order = salesOrderService.createSalesOrder(dto);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Commande de vente créée", order));
    }
    
    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES')")
    public ResponseEntity<ApiResponse<SalesOrderDTO>> confirmSalesOrder(@PathVariable Long id) {
        SalesOrderDTO order = salesOrderService.confirmSalesOrder(id);
        return ResponseEntity.ok(ApiResponse.success("Commande confirmée", order));
    }
    
    @PostMapping("/{id}/payments")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES')")
    public ResponseEntity<ApiResponse<PaymentDTO>> addPayment(
            @PathVariable Long id,
            @Valid @RequestBody PaymentCreateDTO dto) {
        PaymentDTO payment = paymentService.addPayment(id, dto);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Paiement enregistré", payment));
    }
}
