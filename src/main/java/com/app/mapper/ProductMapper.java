package com.app.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.app.dto.ProductDto;
import com.app.dto.ProductUpdateDto;
import com.app.entities.Product;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, SupplierMapper.class})
public interface ProductMapper {
    
    @Mapping(target = "margin", expression = "java(product.getMargin())")
    @Mapping(target = "marginPercentage", expression = "java(product.getMarginPercentage())")
    @Mapping(target = "isLowStock", expression = "java(product.isLowStock())")
    @Mapping(target = "isOutOfStock", expression = "java(product.isOutOfStock())")
    ProductDto toDTO(Product product);
    
    List<ProductDto> toDTOList(List<Product> products);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "quantityInStock", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "purchaseOrderItems", ignore = true)
    @Mapping(target = "salesOrderItems", ignore = true)
    @Mapping(target = "stockMovements", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    void updateEntityFromDTO(ProductUpdateDto dto, @MappingTarget Product product);
}
