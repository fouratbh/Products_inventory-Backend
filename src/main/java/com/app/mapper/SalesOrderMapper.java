package com.app.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.app.dto.SalesOrderDTO;
import com.app.entities.SalesOrder;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class, UserMapper.class, SalesOrderItemMapper.class, PaymentMapper.class})
public interface SalesOrderMapper {
    
    @Mapping(target = "status", expression = "java(salesOrder.getStatus() != null ? salesOrder.getStatus().name() : null)")
    @Mapping(target = "paymentStatus", expression = "java(salesOrder.getPaymentStatus() != null ? salesOrder.getPaymentStatus().name() : null)")
    @Mapping(target = "totalPaid", expression = "java(salesOrder.getTotalPaid())")
    @Mapping(target = "remainingAmount", expression = "java(salesOrder.getRemainingAmount())")
    SalesOrderDTO toDTO(SalesOrder salesOrder);
    
    List<SalesOrderDTO> toDTOList(List<SalesOrder> salesOrders);
}
