package com.app.mapper;

import com.app.dto.PaymentDTO;
import com.app.entities.Payment;
import com.app.entities.SalesOrder;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-03T08:28:46+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class PaymentMapperImpl implements PaymentMapper {

    @Autowired
    private UserMapper userMapper;

    @Override
    public PaymentDTO toDTO(Payment payment) {
        if ( payment == null ) {
            return null;
        }

        PaymentDTO.PaymentDTOBuilder paymentDTO = PaymentDTO.builder();

        paymentDTO.salesOrderId( paymentSalesOrderId( payment ) );
        paymentDTO.id( payment.getId() );
        paymentDTO.paymentDate( payment.getPaymentDate() );
        paymentDTO.amount( payment.getAmount() );
        paymentDTO.reference( payment.getReference() );
        paymentDTO.notes( payment.getNotes() );
        paymentDTO.createdBy( userMapper.toDTO( payment.getCreatedBy() ) );
        paymentDTO.createdAt( payment.getCreatedAt() );

        paymentDTO.paymentMethod( payment.getPaymentMethod() != null ? payment.getPaymentMethod().name() : null );

        return paymentDTO.build();
    }

    @Override
    public List<PaymentDTO> toDTOList(List<Payment> payments) {
        if ( payments == null ) {
            return null;
        }

        List<PaymentDTO> list = new ArrayList<PaymentDTO>( payments.size() );
        for ( Payment payment : payments ) {
            list.add( toDTO( payment ) );
        }

        return list;
    }

    private Long paymentSalesOrderId(Payment payment) {
        if ( payment == null ) {
            return null;
        }
        SalesOrder salesOrder = payment.getSalesOrder();
        if ( salesOrder == null ) {
            return null;
        }
        Long id = salesOrder.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
