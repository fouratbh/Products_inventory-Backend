package com.app.mapper;

import com.app.dto.PurchaseOrderDTO;
import com.app.entities.PurchaseOrder;
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
public class PurchaseOrderMapperImpl implements PurchaseOrderMapper {

    @Autowired
    private SupplierMapper supplierMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PurchaseOrderItemMapper purchaseOrderItemMapper;

    @Override
    public PurchaseOrderDTO toDTO(PurchaseOrder purchaseOrder) {
        if ( purchaseOrder == null ) {
            return null;
        }

        PurchaseOrderDTO.PurchaseOrderDTOBuilder purchaseOrderDTO = PurchaseOrderDTO.builder();

        purchaseOrderDTO.id( purchaseOrder.getId() );
        purchaseOrderDTO.orderNumber( purchaseOrder.getOrderNumber() );
        purchaseOrderDTO.supplier( supplierMapper.toDTO( purchaseOrder.getSupplier() ) );
        purchaseOrderDTO.orderDate( purchaseOrder.getOrderDate() );
        purchaseOrderDTO.deliveryDate( purchaseOrder.getDeliveryDate() );
        purchaseOrderDTO.totalAmount( purchaseOrder.getTotalAmount() );
        purchaseOrderDTO.notes( purchaseOrder.getNotes() );
        purchaseOrderDTO.createdBy( userMapper.toDTO( purchaseOrder.getCreatedBy() ) );
        purchaseOrderDTO.createdAt( purchaseOrder.getCreatedAt() );
        purchaseOrderDTO.updatedAt( purchaseOrder.getUpdatedAt() );
        purchaseOrderDTO.items( purchaseOrderItemMapper.toDTOList( purchaseOrder.getItems() ) );

        purchaseOrderDTO.status( purchaseOrder.getStatus() != null ? purchaseOrder.getStatus().name() : null );
        purchaseOrderDTO.isFullyReceived( purchaseOrder.isFullyReceived() );

        return purchaseOrderDTO.build();
    }

    @Override
    public List<PurchaseOrderDTO> toDTOList(List<PurchaseOrder> purchaseOrders) {
        if ( purchaseOrders == null ) {
            return null;
        }

        List<PurchaseOrderDTO> list = new ArrayList<PurchaseOrderDTO>( purchaseOrders.size() );
        for ( PurchaseOrder purchaseOrder : purchaseOrders ) {
            list.add( toDTO( purchaseOrder ) );
        }

        return list;
    }
}
