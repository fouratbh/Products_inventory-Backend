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
import com.app.dto.SalesOrderCreateDTO;
import com.app.dto.SalesOrderDTO;
import com.app.entities.Customer;
import com.app.entities.Product;
import com.app.entities.SalesOrder;
import com.app.entities.SalesOrderItem;
import com.app.entities.User;
import com.app.exceptions.ResourceNotFoundException;
import com.app.mapper.PageMapper;
import com.app.mapper.SalesOrderMapper;
import com.app.repos.CustomerRepository;
import com.app.repos.ProductRepository;
import com.app.repos.SalesOrderRepository;
import com.app.repos.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SalesOrderService {
    
    private final SalesOrderRepository salesOrderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final StockMovementService stockMovementService;
    private final SalesOrderMapper salesOrderMapper;
    private final PageMapper pageMapper;
    
    @Transactional(readOnly = true)
    public PageResponse<SalesOrderDTO> getAllSalesOrders(Pageable pageable) {
        Page<SalesOrder> page = salesOrderRepository.findAll(pageable);
        List<SalesOrderDTO> content = salesOrderMapper.toDTOList(page.getContent());
        return pageMapper.toPageResponse(page, content);
    }
    
    @Transactional(readOnly = true)
    public SalesOrderDTO getSalesOrderById(Long id) {
        SalesOrder order = salesOrderRepository.findByIdWithDetails(id)
            .orElseThrow(() -> new ResourceNotFoundException("Sales order not found with id: " + id));
        return salesOrderMapper.toDTO(order);
    }
    
    public SalesOrderDTO createSalesOrder(SalesOrderCreateDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        User currentUser = getCurrentUser();
        String orderNumber = generateSalesOrderNumber();
        
        SalesOrder order = SalesOrder.builder()
            .orderNumber(orderNumber)
            .customer(customer)
            .orderDate(dto.getOrderDate())
            .deliveryDate(dto.getDeliveryDate())
            .status(SalesOrder.OrderStatus.DRAFT)
            .discountPercentage(dto.getDiscountPercentage())
            .taxPercentage(dto.getTaxPercentage())
            .paymentStatus(SalesOrder.PaymentStatus.PENDING)
            .notes(dto.getNotes())
            .createdBy(currentUser)
            .build();
        
        dto.getItems().forEach(itemDTO -> {
            Product product = productRepository.findById(itemDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            
            // Check stock availability
            if (product.getQuantityInStock() < itemDTO.getQuantity()) {
                throw new IllegalArgumentException(
                    "Insufficient stock for product: " + product.getName()
                );
            }
            
            SalesOrderItem item = SalesOrderItem.builder()
                .product(product)
                .quantity(itemDTO.getQuantity())
                .unitPrice(itemDTO.getUnitPrice())
                .discountPercentage(itemDTO.getDiscountPercentage())
                .build();
            
            order.addItem(item);
        });
        
        SalesOrder savedOrder = salesOrderRepository.save(order);
        log.info("Sales order created: {}", savedOrder.getOrderNumber());
        
        return salesOrderMapper.toDTO(savedOrder);
    }
    
    public SalesOrderDTO confirmSalesOrder(Long id) {
        SalesOrder order = salesOrderRepository.findByIdWithDetails(id)
            .orElseThrow(() -> new ResourceNotFoundException("Sales order not found"));
        
        if (order.getStatus() != SalesOrder.OrderStatus.DRAFT) {
            throw new IllegalStateException("Only draft orders can be confirmed");
        }
        
        // Update stock for each item
        order.getItems().forEach(item -> {
            productService.updateProductStock(item.getProduct().getId(), -item.getQuantity());
            
            stockMovementService.createSalesStockMovement(
                item.getProduct(), 
                item.getQuantity(), 
                order.getId()
            );
        });
        
        order.setStatus(SalesOrder.OrderStatus.CONFIRMED);
        SalesOrder confirmedOrder = salesOrderRepository.save(order);
        
        log.info("Sales order confirmed: {}", confirmedOrder.getOrderNumber());
        return salesOrderMapper.toDTO(confirmedOrder);
    }
    
    private String generateSalesOrderNumber() {
        String prefix = "SO-" + LocalDate.now().getYear();
        AtomicInteger counter = new AtomicInteger(1);
        
        String orderNumber;
        do {
            orderNumber = String.format("%s-%05d", prefix, counter.getAndIncrement());
        } while (salesOrderRepository.existsByOrderNumber(orderNumber));
        
        return orderNumber;
    }
    
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }
}