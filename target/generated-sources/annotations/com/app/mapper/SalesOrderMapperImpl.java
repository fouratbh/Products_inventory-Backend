package com.app.mapper;

import com.app.dto.SalesOrderDTO;
import com.app.entities.SalesOrder;
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
public class SalesOrderMapperImpl implements SalesOrderMapper {

    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SalesOrderItemMapper salesOrderItemMapper;
    @Autowired
    private PaymentMapper paymentMapper;

    @Override
    public SalesOrderDTO toDTO(SalesOrder salesOrder) {
        if ( salesOrder == null ) {
            return null;
        }

        SalesOrderDTO.SalesOrderDTOBuilder salesOrderDTO = SalesOrderDTO.builder();

        salesOrderDTO.id( salesOrder.getId() );
        salesOrderDTO.orderNumber( salesOrder.getOrderNumber() );
        salesOrderDTO.customer( customerMapper.toDTO( salesOrder.getCustomer() ) );
        salesOrderDTO.orderDate( salesOrder.getOrderDate() );
        salesOrderDTO.deliveryDate( salesOrder.getDeliveryDate() );
        salesOrderDTO.totalAmount( salesOrder.getTotalAmount() );
        salesOrderDTO.discountPercentage( salesOrder.getDiscountPercentage() );
        salesOrderDTO.taxPercentage( salesOrder.getTaxPercentage() );
        salesOrderDTO.finalAmount( salesOrder.getFinalAmount() );
        salesOrderDTO.notes( salesOrder.getNotes() );
        salesOrderDTO.createdBy( userMapper.toDTO( salesOrder.getCreatedBy() ) );
        salesOrderDTO.createdAt( salesOrder.getCreatedAt() );
        salesOrderDTO.updatedAt( salesOrder.getUpdatedAt() );
        salesOrderDTO.items( salesOrderItemMapper.toDTOList( salesOrder.getItems() ) );
        salesOrderDTO.payments( paymentMapper.toDTOList( salesOrder.getPayments() ) );

        salesOrderDTO.status( salesOrder.getStatus() != null ? salesOrder.getStatus().name() : null );
        salesOrderDTO.paymentStatus( salesOrder.getPaymentStatus() != null ? salesOrder.getPaymentStatus().name() : null );
        salesOrderDTO.totalPaid( salesOrder.getTotalPaid() );
        salesOrderDTO.remainingAmount( salesOrder.getRemainingAmount() );

        return salesOrderDTO.build();
    }

    @Override
    public List<SalesOrderDTO> toDTOList(List<SalesOrder> salesOrders) {
        if ( salesOrders == null ) {
            return null;
        }

        List<SalesOrderDTO> list = new ArrayList<SalesOrderDTO>( salesOrders.size() );
        for ( SalesOrder salesOrder : salesOrders ) {
            list.add( toDTO( salesOrder ) );
        }

        return list;
    }
}
