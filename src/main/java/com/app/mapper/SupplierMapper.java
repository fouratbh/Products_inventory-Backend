package com.app.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.app.dto.SupplierCreateDto;
import com.app.dto.SupplierDto;
import com.app.entities.Supplier;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    SupplierDto toDTO(Supplier supplier);
    
    List<SupplierDto> toDTOList(List<Supplier> suppliers);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "purchaseOrders", ignore = true)
    Supplier toEntity(SupplierCreateDto dto);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "purchaseOrders", ignore = true)
    void updateEntityFromDTO(SupplierCreateDto dto, @MappingTarget Supplier supplier);
}
