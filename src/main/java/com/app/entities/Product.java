package com.app.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

// ============= PRODUCT ENTITY =============
@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_product_code", columnList = "code"),
    @Index(name = "idx_product_category", columnList = "category_id"),
    @Index(name = "idx_product_supplier", columnList = "supplier_id")
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @Column(name = "purchase_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal purchasePrice;

    @Column(name = "selling_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "quantity_in_stock", nullable = false)
    @Builder.Default
    private Integer quantityInStock = 0;

    @Column(name = "min_stock_level")
    @Builder.Default
    private Integer minStockLevel = 10;

    @Column(name = "max_stock_level")
    @Builder.Default
    private Integer maxStockLevel = 1000;

    @Column(length = 20)
    @Builder.Default
    private String unit = "pièce";

    @Column(name = "warranty_months")
    @Builder.Default
    private Integer warrantyMonths = 12;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<PurchaseOrderItem> purchaseOrderItems = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<SalesOrderItem> salesOrderItems = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<StockMovement> stockMovements = new HashSet<>();

    // Business methods
    public boolean isLowStock() {
        return this.quantityInStock != null && 
               this.minStockLevel != null && 
               this.quantityInStock <= this.minStockLevel;
    }

    public boolean isOutOfStock() {
        return this.quantityInStock == null || this.quantityInStock == 0;
    }

    public BigDecimal getMargin() {
        if (purchasePrice == null || sellingPrice == null) {
            return BigDecimal.ZERO;
        }
        return sellingPrice.subtract(purchasePrice);
    }

    public BigDecimal getMarginPercentage() {
        if (purchasePrice == null || purchasePrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return getMargin().divide(purchasePrice, 4, BigDecimal.ROUND_HALF_UP)
                         .multiply(BigDecimal.valueOf(100));
    }
}