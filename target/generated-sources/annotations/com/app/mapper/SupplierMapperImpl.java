package com.app.mapper;

import com.app.dto.SupplierCreateDto;
import com.app.dto.SupplierDto;
import com.app.entities.Supplier;
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
public class SupplierMapperImpl implements SupplierMapper {

    @Override
    public SupplierDto toDTO(Supplier supplier) {
        if ( supplier == null ) {
            return null;
        }

        SupplierDto.SupplierDtoBuilder supplierDto = SupplierDto.builder();

        supplierDto.id( supplier.getId() );
        supplierDto.name( supplier.getName() );
        supplierDto.contactPerson( supplier.getContactPerson() );
        supplierDto.email( supplier.getEmail() );
        supplierDto.phone( supplier.getPhone() );
        supplierDto.address( supplier.getAddress() );
        supplierDto.city( supplier.getCity() );
        supplierDto.country( supplier.getCountry() );
        supplierDto.createdAt( supplier.getCreatedAt() );
        supplierDto.updatedAt( supplier.getUpdatedAt() );

        return supplierDto.build();
    }

    @Override
    public List<SupplierDto> toDTOList(List<Supplier> suppliers) {
        if ( suppliers == null ) {
            return null;
        }

        List<SupplierDto> list = new ArrayList<SupplierDto>( suppliers.size() );
        for ( Supplier supplier : suppliers ) {
            list.add( toDTO( supplier ) );
        }

        return list;
    }

    @Override
    public Supplier toEntity(SupplierCreateDto dto) {
        if ( dto == null ) {
            return null;
        }

        Supplier.SupplierBuilder supplier = Supplier.builder();

        supplier.name( dto.getName() );
        supplier.contactPerson( dto.getContactPerson() );
        supplier.email( dto.getEmail() );
        supplier.phone( dto.getPhone() );
        supplier.address( dto.getAddress() );
        supplier.city( dto.getCity() );
        supplier.country( dto.getCountry() );

        return supplier.build();
    }

    @Override
    public void updateEntityFromDTO(SupplierCreateDto dto, Supplier supplier) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getName() != null ) {
            supplier.setName( dto.getName() );
        }
        if ( dto.getContactPerson() != null ) {
            supplier.setContactPerson( dto.getContactPerson() );
        }
        if ( dto.getEmail() != null ) {
            supplier.setEmail( dto.getEmail() );
        }
        if ( dto.getPhone() != null ) {
            supplier.setPhone( dto.getPhone() );
        }
        if ( dto.getAddress() != null ) {
            supplier.setAddress( dto.getAddress() );
        }
        if ( dto.getCity() != null ) {
            supplier.setCity( dto.getCity() );
        }
        if ( dto.getCountry() != null ) {
            supplier.setCountry( dto.getCountry() );
        }
    }
}
