package com.app.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.app.dto.PurchaseOrderItemDTO;
import com.app.entities.PurchaseOrderItem;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface PurchaseOrderItemMapper {
    
    @Mapping(target = "remainingQuantity", expression = "java(item.getRemainingQuantity())")
    PurchaseOrderItemDTO toDTO(PurchaseOrderItem item);
    
    List<PurchaseOrderItemDTO> toDTOList(List<PurchaseOrderItem> items);
}
