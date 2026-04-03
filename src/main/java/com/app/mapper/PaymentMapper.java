package com.app.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.app.dto.PaymentDTO;
import com.app.entities.Payment;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface PaymentMapper {
    
    @Mapping(target = "salesOrderId", source = "salesOrder.id")
    @Mapping(target = "paymentMethod", expression = "java(payment.getPaymentMethod() != null ? payment.getPaymentMethod().name() : null)")
    PaymentDTO toDTO(Payment payment);
    
    List<PaymentDTO> toDTOList(List<Payment> payments);
}
