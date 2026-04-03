package com.app.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.app.dto.CustomerCreateDto;
import com.app.dto.CustomerDto;
import com.app.entities.Customer;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    
    @Mapping(target = "customerType", expression = "java(customer.getCustomerType() != null ? customer.getCustomerType().name() : null)")
    CustomerDto toDTO(Customer customer);
    
    List<CustomerDto> toDTOList(List<Customer> customers);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "salesOrders", ignore = true)
    @Mapping(target = "customerType", expression = "java(com.app.entities.Customer.CustomerType.valueOf(dto.getCustomerType()))")
    Customer toEntity(CustomerCreateDto dto);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "salesOrders", ignore = true)
    @Mapping(target = "customerType", expression = "java(dto.getCustomerType() != null ? com.app.entities.Customer.CustomerType.valueOf(dto.getCustomerType()) : null)")
    void updateEntityFromDTO(CustomerCreateDto dto, @MappingTarget Customer customer);
}
