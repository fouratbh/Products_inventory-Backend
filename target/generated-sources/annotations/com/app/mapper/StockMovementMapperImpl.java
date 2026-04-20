package com.app.mapper;

import com.app.dto.StockMovementDTO;
import com.app.entities.StockMovement;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-20T17:26:59+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Microsoft)"
)
@Component
public class StockMovementMapperImpl implements StockMovementMapper {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public StockMovementDTO toDTO(StockMovement movement) {
        if ( movement == null ) {
            return null;
        }

        StockMovementDTO.StockMovementDTOBuilder stockMovementDTO = StockMovementDTO.builder();

        stockMovementDTO.id( movement.getId() );
        stockMovementDTO.product( productMapper.toDTO( movement.getProduct() ) );
        stockMovementDTO.quantity( movement.getQuantity() );
        stockMovementDTO.referenceType( movement.getReferenceType() );
        stockMovementDTO.referenceId( movement.getReferenceId() );
        stockMovementDTO.notes( movement.getNotes() );
        stockMovementDTO.createdBy( userMapper.toDTO( movement.getCreatedBy() ) );
        stockMovementDTO.createdAt( movement.getCreatedAt() );

        stockMovementDTO.movementType( movement.getMovementType() != null ? movement.getMovementType().name() : null );

        return stockMovementDTO.build();
    }

    @Override
    public List<StockMovementDTO> toDTOList(List<StockMovement> movements) {
        if ( movements == null ) {
            return null;
        }

        List<StockMovementDTO> list = new ArrayList<StockMovementDTO>( movements.size() );
        for ( StockMovement stockMovement : movements ) {
            list.add( toDTO( stockMovement ) );
        }

        return list;
    }
}
