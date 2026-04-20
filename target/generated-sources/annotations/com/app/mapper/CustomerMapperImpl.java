package com.app.mapper;

import com.app.dto.CustomerCreateDto;
import com.app.dto.CustomerDto;
import com.app.entities.Customer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-20T17:26:58+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Microsoft)"
)
@Component
public class CustomerMapperImpl implements CustomerMapper {

    @Override
    public CustomerDto toDTO(Customer customer) {
        if ( customer == null ) {
            return null;
        }

        Long id = null;
        String code = null;
        String name = null;
        String contactPerson = null;
        String email = null;
        String phone = null;
        String address = null;
        String city = null;
        String country = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        id = customer.getId();
        code = customer.getCode();
        name = customer.getName();
        contactPerson = customer.getContactPerson();
        email = customer.getEmail();
        phone = customer.getPhone();
        address = customer.getAddress();
        city = customer.getCity();
        country = customer.getCountry();
        createdAt = customer.getCreatedAt();
        updatedAt = customer.getUpdatedAt();

        String customerType = customer.getCustomerType() != null ? customer.getCustomerType().name() : null;

        CustomerDto customerDto = new CustomerDto( id, code, name, contactPerson, email, phone, address, city, country, customerType, createdAt, updatedAt );

        return customerDto;
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
