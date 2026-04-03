package com.app.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.app.dto.PurchaseOrderDTO;
import com.app.entities.PurchaseOrder;

@Mapper(componentModel = "spring", uses = {SupplierMapper.class, UserMapper.class, PurchaseOrderItemMapper.class})
public interface PurchaseOrderMapper {
    
    @Mapping(target = "status", expression = "java(purchaseOrder.getStatus() != null ? purchaseOrder.getStatus().name() : null)")
    @Mapping(target = "isFullyReceived", expression = "java(purchaseOrder.isFullyReceived())")
    PurchaseOrderDTO toDTO(PurchaseOrder purchaseOrder);
    
    List<PurchaseOrderDTO> toDTOList(List<PurchaseOrder> purchaseOrders);
}
