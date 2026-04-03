package com.app.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dto.PaymentCreateDTO;
import com.app.dto.PaymentDTO;
import com.app.entities.Payment;
import com.app.entities.SalesOrder;
import com.app.entities.User;
import com.app.exceptions.ResourceNotFoundException;
import com.app.mapper.PaymentMapper;
import com.app.repos.PaymentRepository;
import com.app.repos.SalesOrderRepository;
import com.app.repos.UserRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final UserRepository userRepository;
    private final PaymentMapper paymentMapper;
    
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsBySalesOrder(Long salesOrderId) {
        List<Payment> payments = paymentRepository.findBySalesOrderId(salesOrderId);
        return paymentMapper.toDTOList(payments);
    }
    
    public PaymentDTO addPayment(Long salesOrderId, PaymentCreateDTO dto) {
        SalesOrder salesOrder = salesOrderRepository.findById(salesOrderId)
            .orElseThrow(() -> new ResourceNotFoundException("Sales order not found"));
        
        // Vérifier que le montant ne dépasse pas le restant dû
        BigDecimal remainingAmount = salesOrder.getRemainingAmount();
        if (dto.getAmount().compareTo(remainingAmount) > 0) {
            throw new IllegalArgumentException(
                "Payment amount exceeds remaining amount: " + remainingAmount
            );
        }
        
        User currentUser = getCurrentUser();
        
        Payment payment = Payment.builder()
            .salesOrder(salesOrder)
            .paymentDate(dto.getPaymentDate())
            .amount(dto.getAmount())
            .paymentMethod(Payment.PaymentMethod.valueOf(dto.getPaymentMethod()))
            .reference(dto.getReference())
            .notes(dto.getNotes())
            .createdBy(currentUser)
            .build();
        
        Payment savedPayment = paymentRepository.save(payment);
        
        // Mettre à jour le statut de paiement de la commande
        updateSalesOrderPaymentStatus(salesOrder);
        
        log.info("Payment added to sales order {}: {} {}", 
            salesOrder.getOrderNumber(), dto.getAmount(), dto.getPaymentMethod());
        
        return paymentMapper.toDTO(savedPayment);
    }
    
    private void updateSalesOrderPaymentStatus(SalesOrder salesOrder) {
        BigDecimal totalPaid = salesOrder.getTotalPaid();
        BigDecimal finalAmount = salesOrder.getFinalAmount();
        
        if (totalPaid.compareTo(BigDecimal.ZERO) == 0) {
            salesOrder.setPaymentStatus(SalesOrder.PaymentStatus.PENDING);
        } else if (totalPaid.compareTo(finalAmount) >= 0) {
            salesOrder.setPaymentStatus(SalesOrder.PaymentStatus.PAID);
            salesOrder.setStatus(SalesOrder.OrderStatus.PAID);
        } else {
            salesOrder.setPaymentStatus(SalesOrder.PaymentStatus.PARTIAL);
        }
        
        salesOrderRepository.save(salesOrder);
    }
    
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }
}