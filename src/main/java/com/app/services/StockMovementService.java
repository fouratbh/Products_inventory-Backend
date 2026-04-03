package com.app.services;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dto.StockMovementDTO;
import com.app.entities.Product;
import com.app.entities.StockMovement;
import com.app.entities.User;
import com.app.exceptions.ResourceNotFoundException;
import com.app.mapper.StockMovementMapper;
import com.app.repos.StockMovementRepository;
import com.app.repos.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StockMovementService {
    
    private final StockMovementRepository stockMovementRepository;
    private final UserRepository userRepository;
    private final StockMovementMapper stockMovementMapper;
    
    @Transactional(readOnly = true)
    public List<StockMovementDTO> getProductMovements(Long productId) {
        return stockMovementRepository.findByProductIdOrderByCreatedAtDesc(productId)
            .stream()
            .map(stockMovementMapper::toDTO)
            .toList();
    }
    
    void createInitialStockMovement(Product product, Integer quantity) {
        User currentUser = getCurrentUser();
        
        StockMovement movement = StockMovement.builder()
            .product(product)
            .movementType(StockMovement.MovementType.IN)
            .quantity(quantity)
            .referenceType("INITIAL_STOCK")
            .notes("Stock initial")
            .createdBy(currentUser)
            .build();
        
        stockMovementRepository.save(movement);
    }
    
    void createPurchaseStockMovement(Product product, Integer quantity, Long purchaseOrderId) {
        User currentUser = getCurrentUser();
        
        StockMovement movement = StockMovement.createPurchaseMovement(
            product, quantity, purchaseOrderId, currentUser
        );
        
        stockMovementRepository.save(movement);
    }
    
    void createSalesStockMovement(Product product, Integer quantity, Long salesOrderId) {
        User currentUser = getCurrentUser();
        
        StockMovement movement = StockMovement.createSalesMovement(
            product, quantity, salesOrderId, currentUser
        );
        
        stockMovementRepository.save(movement);
    }
    
    void createAdjustmentMovement(Product product, Integer quantity, String notes) {
        User currentUser = getCurrentUser();
        
        StockMovement movement = StockMovement.createAdjustmentMovement(
            product, quantity, notes, currentUser
        );
        
        stockMovementRepository.save(movement);
    }
    
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }
}