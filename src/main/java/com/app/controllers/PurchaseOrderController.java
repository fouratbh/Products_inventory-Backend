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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Sort;


import com.app.dto.ApiResponse;
import com.app.dto.PageResponse;
import com.app.dto.PurchaseOrderCreateDTO;
import com.app.dto.PurchaseOrderDTO;
import com.app.dto.PurchaseOrderUpdateDTO;
import com.app.dto.ReceiveProductDTO;
import com.app.services.PurchaseOrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {
    
    private final PurchaseOrderService purchaseOrderService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE')")
    public ResponseEntity<ApiResponse<PageResponse<PurchaseOrderDTO>>> getAllPurchaseOrders(
            @PageableDefault(size = 20, sort = "orderDate", direction = Sort.Direction.DESC) 
            Pageable pageable) {
        PageResponse<PurchaseOrderDTO> orders = purchaseOrderService.getAllPurchaseOrders(pageable);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE')")
    public ResponseEntity<ApiResponse<PurchaseOrderDTO>> getPurchaseOrderById(@PathVariable Long id) {
        PurchaseOrderDTO order = purchaseOrderService.getPurchaseOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(order));
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<PurchaseOrderDTO>> createPurchaseOrder(
            @Valid @RequestBody PurchaseOrderCreateDTO dto) {
        PurchaseOrderDTO order = purchaseOrderService.createPurchaseOrder(dto);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Commande d'achat créée", order));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<PurchaseOrderDTO>> updatePurchaseOrder(
            @PathVariable Long id,
            @Valid @RequestBody PurchaseOrderUpdateDTO dto) {
        PurchaseOrderDTO order = purchaseOrderService.updatePurchaseOrder(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Commande mise à jour", order));
    }
    
    @PostMapping("/{orderId}/items/{itemId}/receive")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE')")
    public ResponseEntity<ApiResponse<PurchaseOrderDTO>> receiveProducts(
            @PathVariable Long orderId,
            @PathVariable Long itemId,
            @Valid @RequestBody ReceiveProductDTO dto) {
        PurchaseOrderDTO order = purchaseOrderService.receiveProducts(orderId, itemId, dto);
        return ResponseEntity.ok(ApiResponse.success("Produits réceptionnés", order));
    }
}
