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
    
    public void createInitialStockMovement(Product product, Integer quantity) {
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
    
    public void createAdjustmentMovement(Product product, Integer quantity, String notes) {
        User currentUser = getCurrentUser();
        
        StockMovement movement = StockMovement.createAdjustmentMovement(
            product, quantity, notes, currentUser
        );
        
        stockMovementRepository.save(movement);
    }
    
    private User getCurrentUser() {
        String keycloakId = SecurityContextHolder.getContext().getAuthentication().getName();
        
        return userRepository.findByUsername(keycloakId)
            .orElseGet(() -> {
                log.info("Création de l'utilisateur local pour l'ID Keycloak : {}", keycloakId);
                User newUser = User.builder()
                    .username(keycloakId)
                    .email(keycloakId + "@system.local") // 👈 ON AJOUTE ÇA POUR ÉVITER LE NOT NULL
                    .enabled(true)
                    .firstName("Utilisateur") // Optionnel, pour éviter d'autres nulls si contraints
                    .lastName("Keycloak")
                    .password("external_auth") // Valeur bidon car l'auth est gérée par Keycloak
                    .build();
                return userRepository.save(newUser);
            });
    }
}