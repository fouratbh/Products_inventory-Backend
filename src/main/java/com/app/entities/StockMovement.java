package com.app.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// ============= STOCK MOVEMENT ENTITY =============
@Entity
@Table(name = "stock_movements", indexes = {
    @Index(name = "idx_stock_product", columnList = "product_id")
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class StockMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", length = 20)
    private MovementType movementType;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "reference_type", length = 30)
    private String referenceType;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum MovementType {
        IN,         
        OUT,        
        ADJUSTMENT, 
        RETURN      
    }

    // Helper methods
    public static StockMovement createPurchaseMovement(Product product, Integer quantity, 
                                                       Long purchaseOrderId, User user) {
        return StockMovement.builder()
            .product(product)
            .movementType(MovementType.IN)
            .quantity(quantity)
            .referenceType("PURCHASE_ORDER")
            .referenceId(purchaseOrderId)
            .createdBy(user)
            .build();
    }

    public static StockMovement createSalesMovement(Product product, Integer quantity, 
                                                    Long salesOrderId, User user) {
        return StockMovement.builder()
            .product(product)
            .movementType(MovementType.OUT)
            .quantity(quantity)
            .referenceType("SALES_ORDER")
            .referenceId(salesOrderId)
            .createdBy(user)
            .build();
    }

    public static StockMovement createAdjustmentMovement(Product product, Integer quantity, 
                                                         String notes, User user) {
        return StockMovement.builder()
            .product(product)
            .movementType(MovementType.ADJUSTMENT)
            .quantity(quantity)
            .referenceType("MANUAL_ADJUSTMENT")
            .notes(notes)
            .createdBy(user)
            .build();
    }
}