package com.app.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.app.dto.SalesOrderItemDTO;
import com.app.entities.SalesOrderItem;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface SalesOrderItemMapper {
    SalesOrderItemDTO toDTO(SalesOrderItem item);
    
    List<SalesOrderItemDTO> toDTOList(List<SalesOrderItem> items);
}
