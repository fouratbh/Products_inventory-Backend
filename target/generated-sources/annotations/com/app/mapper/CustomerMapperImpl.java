package com.app.mapper;

import com.app.dto.CustomerCreateDto;
import com.app.dto.CustomerDto;
import com.app.entities.Customer;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-03T06:16:04+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class CustomerMapperImpl implements CustomerMapper {

    @Override
    public CustomerDto toDTO(Customer customer) {
        if ( customer == null ) {
            return null;
        }

        CustomerDto.CustomerDtoBuilder customerDto = CustomerDto.builder();

        customerDto.id( customer.getId() );
        customerDto.code( customer.getCode() );
        customerDto.name( customer.getName() );
        customerDto.contactPerson( customer.getContactPerson() );
        customerDto.email( customer.getEmail() );
        customerDto.phone( customer.getPhone() );
        customerDto.address( customer.getAddress() );
        customerDto.city( customer.getCity() );
        customerDto.country( customer.getCountry() );
        customerDto.createdAt( customer.getCreatedAt() );
        customerDto.updatedAt( customer.getUpdatedAt() );

        customerDto.customerType( customer.getCustomerType() != null ? customer.getCustomerType().name() : null );

        return customerDto.build();
    }

    @Override
    public List<CustomerDto> toDTOList(List<Customer> customers) {
        if ( customers == null ) {
            return null;
        }

        List<CustomerDto> list = new ArrayList<CustomerDto>( customers.size() );
        for ( Customer customer : customers ) {
            list.add( toDTO( customer ) );
        }

        return list;
    }

    @Override
    public Customer toEntity(CustomerCreateDto dto) {
        if ( dto == null ) {
            return null;
        }

        Customer.CustomerBuilder customer = Customer.builder();

        customer.code( dto.getCode() );
        customer.name( dto.getName() );
        customer.contactPerson( dto.getContactPerson() );
        customer.email( dto.getEmail() );
        customer.phone( dto.getPhone() );
        customer.address( dto.getAddress() );
        customer.city( dto.getCity() );
        customer.country( dto.getCountry() );

        customer.customerType( com.app.entities.Customer.CustomerType.valueOf(dto.getCustomerType()) );

        return customer.build();
    }

    @Override
    public void updateEntityFromDTO(CustomerCreateDto dto, Customer customer) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getName() != null ) {
            customer.setName( dto.getName() );
        }
        if ( dto.getContactPerson() != null ) {
            customer.setContactPerson( dto.getContactPerson() );
        }
        if ( dto.getEmail() != null ) {
            customer.setEmail( dto.getEmail() );
        }
        if ( dto.getPhone() != null ) {
            customer.setPhone( dto.getPhone() );
        }
        if ( dto.getAddress() != null ) {
            customer.setAddress( dto.getAddress() );
        }
        if ( dto.getCity() != null ) {
            customer.setCity( dto.getCity() );
        }
        if ( dto.getCountry() != null ) {
            customer.setCountry( dto.getCountry() );
        }

        customer.setCustomerType( dto.getCustomerType() != null ? com.app.entities.Customer.CustomerType.valueOf(dto.getCustomerType()) : null );
    }
}
