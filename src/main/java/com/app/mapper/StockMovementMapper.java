package com.app.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.app.dto.StockMovementDTO;
import com.app.entities.StockMovement;

@Mapper(componentModel = "spring", uses = {ProductMapper.class, UserMapper.class})
public interface StockMovementMapper {
    
    @Mapping(target = "movementType", expression = "java(movement.getMovementType() != null ? movement.getMovementType().name() : null)")
    StockMovementDTO toDTO(StockMovement movement);
    
    List<StockMovementDTO> toDTOList(List<StockMovement> movements);
}
