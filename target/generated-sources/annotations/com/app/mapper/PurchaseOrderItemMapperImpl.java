package com.app.mapper;

import com.app.dto.PurchaseOrderItemDTO;
import com.app.entities.PurchaseOrderItem;
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
public class PurchaseOrderItemMapperImpl implements PurchaseOrderItemMapper {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public PurchaseOrderItemDTO toDTO(PurchaseOrderItem item) {
        if ( item == null ) {
            return null;
        }

        PurchaseOrderItemDTO.PurchaseOrderItemDTOBuilder purchaseOrderItemDTO = PurchaseOrderItemDTO.builder();

        purchaseOrderItemDTO.id( item.getId() );
        purchaseOrderItemDTO.product( productMapper.toDTO( item.getProduct() ) );
        purchaseOrderItemDTO.quantity( item.getQuantity() );
        purchaseOrderItemDTO.unitPrice( item.getUnitPrice() );
        purchaseOrderItemDTO.totalPrice( item.getTotalPrice() );
        purchaseOrderItemDTO.receivedQuantity( item.getReceivedQuantity() );

        purchaseOrderItemDTO.remainingQuantity( item.getRemainingQuantity() );

        return purchaseOrderItemDTO.build();
    }

    @Override
    public List<PurchaseOrderItemDTO> toDTOList(List<PurchaseOrderItem> items) {
        if ( items == null ) {
            return null;
        }

        List<PurchaseOrderItemDTO> list = new ArrayList<PurchaseOrderItemDTO>( items.size() );
        for ( PurchaseOrderItem purchaseOrderItem : items ) {
            list.add( toDTO( purchaseOrderItem ) );
        }

        return list;
    }
}
