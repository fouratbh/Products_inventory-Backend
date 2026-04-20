package com.app.mapper;

import com.app.dto.PaymentDTO;
import com.app.dto.UserDto;
import com.app.entities.Payment;
import com.app.entities.SalesOrder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class PaymentMapperImpl implements PaymentMapper {

    @Autowired
    private UserMapper userMapper;

    @Override
    public PaymentDTO toDTO(Payment payment) {
        if ( payment == null ) {
            return null;
        }

        Long salesOrderId = null;
        Long id = null;
        LocalDate paymentDate = null;
        BigDecimal amount = null;
        String reference = null;
        String notes = null;
        UserDto createdBy = null;
        LocalDateTime createdAt = null;

        salesOrderId = paymentSalesOrderId( payment );
        id = payment.getId();
        paymentDate = payment.getPaymentDate();
        amount = payment.getAmount();
        reference = payment.getReference();
        notes = payment.getNotes();
        createdBy = userMapper.toDTO( payment.getCreatedBy() );
        createdAt = payment.getCreatedAt();

        String paymentMethod = payment.getPaymentMethod() != null ? payment.getPaymentMethod().name() : null;

        PaymentDTO paymentDTO = new PaymentDTO( id, salesOrderId, paymentDate, amount, paymentMethod, reference, notes, createdBy, createdAt );

        return paymentDTO;
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
