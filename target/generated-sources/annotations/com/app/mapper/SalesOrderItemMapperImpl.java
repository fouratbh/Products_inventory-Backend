package com.app.mapper;

import com.app.dto.SalesOrderItemDTO;
import com.app.entities.SalesOrderItem;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-03T06:16:04+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class SalesOrderItemMapperImpl implements SalesOrderItemMapper {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public SalesOrderItemDTO toDTO(SalesOrderItem item) {
        if ( item == null ) {
            return null;
        }

        SalesOrderItemDTO.SalesOrderItemDTOBuilder salesOrderItemDTO = SalesOrderItemDTO.builder();

        salesOrderItemDTO.id( item.getId() );
        salesOrderItemDTO.product( productMapper.toDTO( item.getProduct() ) );
        salesOrderItemDTO.quantity( item.getQuantity() );
        salesOrderItemDTO.unitPrice( item.getUnitPrice() );
        salesOrderItemDTO.discountPercentage( item.getDiscountPercentage() );
        salesOrderItemDTO.totalPrice( item.getTotalPrice() );

        return salesOrderItemDTO.build();
    }

    @Override
    public List<SalesOrderItemDTO> toDTOList(List<SalesOrderItem> items) {
        if ( items == null ) {
            return null;
        }

        List<SalesOrderItemDTO> list = new ArrayList<SalesOrderItemDTO>( items.size() );
        for ( SalesOrderItem salesOrderItem : items ) {
            list.add( toDTO( salesOrderItem ) );
        }

        return list;
    }
}
