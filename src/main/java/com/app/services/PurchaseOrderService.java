package com.app.services;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dto.PageResponse;
import com.app.dto.PurchaseOrderCreateDTO;
import com.app.dto.PurchaseOrderDTO;
import com.app.dto.PurchaseOrderUpdateDTO;
import com.app.dto.ReceiveProductDTO;
import com.app.entities.Product;
import com.app.entities.PurchaseOrder;
import com.app.entities.PurchaseOrderItem;
import com.app.entities.Supplier;
import com.app.entities.User;
import com.app.exceptions.ResourceNotFoundException;
import com.app.mapper.PageMapper;
import com.app.mapper.PurchaseOrderMapper;
import com.app.repos.ProductRepository;
import com.app.repos.PurchaseOrderRepository;
import com.app.repos.SupplierRepository;
import com.app.repos.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PurchaseOrderService {
    
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final StockMovementService stockMovementService;
    private final PurchaseOrderMapper purchaseOrderMapper;
    private final PageMapper pageMapper;
    
    @Transactional(readOnly = true)
    public PageResponse<PurchaseOrderDTO> getAllPurchaseOrders(Pageable pageable) {
        Page<PurchaseOrder> page = purchaseOrderRepository.findAll(pageable);
        List<PurchaseOrderDTO> content = purchaseOrderMapper.toDTOList(page.getContent());
        return pageMapper.toPageResponse(page, content);
    }
    
    @Transactional(readOnly = true)
    public PurchaseOrderDTO getPurchaseOrderById(Long id) {
        PurchaseOrder order = purchaseOrderRepository.findByIdWithItems(id)
            .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with id: " + id));
        return purchaseOrderMapper.toDTO(order);
    }
    
    public PurchaseOrderDTO createPurchaseOrder(PurchaseOrderCreateDTO dto) {
        Supplier supplier = supplierRepository.findById(dto.getSupplierId())
            .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
        
        User currentUser = getCurrentUser();
        
        String orderNumber = generatePurchaseOrderNumber();
        
        PurchaseOrder order = PurchaseOrder.builder()
            .orderNumber(orderNumber)
            .supplier(supplier)
            .orderDate(dto.getOrderDate())
            .deliveryDate(dto.getDeliveryDate())
            .status(PurchaseOrder.OrderStatus.PENDING)
            .notes(dto.getNotes())
            .createdBy(currentUser)
            .build();
        
        dto.getItems().forEach(itemDTO -> {
            Product product = productRepository.findById(itemDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            
            PurchaseOrderItem item = PurchaseOrderItem.builder()
                .product(product)
                .quantity(itemDTO.getQuantity())
                .unitPrice(itemDTO.getUnitPrice())
                .build();
            
            order.addItem(item);
        });
        
        PurchaseOrder savedOrder = purchaseOrderRepository.save(order);
        log.info("Purchase order created: {}", savedOrder.getOrderNumber());
        
        return purchaseOrderMapper.toDTO(savedOrder);
    }
    
    public PurchaseOrderDTO updatePurchaseOrder(Long id, PurchaseOrderUpdateDTO dto) {
        PurchaseOrder order = purchaseOrderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found"));
        
        if (dto.getDeliveryDate() != null) {
            order.setDeliveryDate(dto.getDeliveryDate());
        }
        
        if (dto.getStatus() != null) {
            order.setStatus(PurchaseOrder.OrderStatus.valueOf(dto.getStatus()));
        }
        
        if (dto.getNotes() != null) {
            order.setNotes(dto.getNotes());
        }
        
        PurchaseOrder updatedOrder = purchaseOrderRepository.save(order);
        log.info("Purchase order updated: {}", updatedOrder.getOrderNumber());
        
        return purchaseOrderMapper.toDTO(updatedOrder);
    }
    
    public PurchaseOrderDTO receiveProducts(Long orderId, Long itemId, ReceiveProductDTO dto) {
        PurchaseOrder order = purchaseOrderRepository.findByIdWithItems(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found"));
        
        PurchaseOrderItem item = order.getItems().stream()
            .filter(i -> i.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Item not found in order"));
        
        int remaining = item.getRemainingQuantity();
        if (dto.getQuantity() > remaining) {
            throw new IllegalArgumentException(
                "Cannot receive more than remaining quantity: " + remaining
            );
        }
        
        item.setReceivedQuantity(item.getReceivedQuantity() + dto.getQuantity());
        
        
        productService.updateProductStock(item.getProduct().getId(), dto.getQuantity());
        
        
        stockMovementService.createPurchaseStockMovement(
            item.getProduct(), 
            dto.getQuantity(), 
            order.getId()
        );
        
        
        if (order.isFullyReceived()) {
            order.setStatus(PurchaseOrder.OrderStatus.DELIVERED);
        }
        
        PurchaseOrder updatedOrder = purchaseOrderRepository.save(order);
        log.info("Products received for order {}: {} units of {}", 
            order.getOrderNumber(), dto.getQuantity(), item.getProduct().getName());
        
        return purchaseOrderMapper.toDTO(updatedOrder);
    }
    
    private String generatePurchaseOrderNumber() {
        String prefix = "PO-" + LocalDate.now().getYear();
        AtomicInteger counter = new AtomicInteger(1);
        
        String orderNumber;
        do {
            orderNumber = String.format("%s-%05d", prefix, counter.getAndIncrement());
        } while (purchaseOrderRepository.existsByOrderNumber(orderNumber));
        
        return orderNumber;
    }
    
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }
}