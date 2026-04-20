package com.app.mapper;

import com.app.dto.ProductDto;
import com.app.dto.ProductUpdateDto;
import com.app.entities.Product;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-20T17:26:58+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Microsoft)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private SupplierMapper supplierMapper;

    @Override
    public ProductDto toDTO(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductDto.ProductDtoBuilder productDto = ProductDto.builder();

        productDto.id( product.getId() );
        productDto.code( product.getCode() );
        productDto.name( product.getName() );
        productDto.description( product.getDescription() );
        productDto.category( categoryMapper.toDTO( product.getCategory() ) );
        productDto.supplier( supplierMapper.toDTO( product.getSupplier() ) );
        productDto.purchasePrice( product.getPurchasePrice() );
        productDto.sellingPrice( product.getSellingPrice() );
        productDto.quantityInStock( product.getQuantityInStock() );
        productDto.minStockLevel( product.getMinStockLevel() );
        productDto.maxStockLevel( product.getMaxStockLevel() );
        productDto.unit( product.getUnit() );
        productDto.warrantyMonths( product.getWarrantyMonths() );
        productDto.createdAt( product.getCreatedAt() );
        productDto.updatedAt( product.getUpdatedAt() );
        productDto.imageUrl( product.getImageUrl() );

        productDto.margin( product.getMargin() );
        productDto.marginPercentage( product.getMarginPercentage() );
        productDto.isLowStock( product.isLowStock() );
        productDto.isOutOfStock( product.isOutOfStock() );

        return productDto.build();
    }

    @Override
    public List<ProductDto> toDTOList(List<Product> products) {
        if ( products == null ) {
            return null;
        }

        List<ProductDto> list = new ArrayList<ProductDto>( products.size() );
        for ( Product product : products ) {
            list.add( toDTO( product ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromDTO(ProductUpdateDto dto, Product product) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getName() != null ) {
            product.setName( dto.getName() );
        }
        if ( dto.getDescription() != null ) {
            product.setDescription( dto.getDescription() );
        }
        if ( dto.getPurchasePrice() != null ) {
            product.setPurchasePrice( dto.getPurchasePrice() );
        }
        if ( dto.getSellingPrice() != null ) {
            product.setSellingPrice( dto.getSellingPrice() );
        }
        if ( dto.getMinStockLevel() != null ) {
            product.setMinStockLevel( dto.getMinStockLevel() );
        }
        if ( dto.getMaxStockLevel() != null ) {
            product.setMaxStockLevel( dto.getMaxStockLevel() );
        }
        if ( dto.getUnit() != null ) {
            product.setUnit( dto.getUnit() );
        }
        if ( dto.getWarrantyMonths() != null ) {
            product.setWarrantyMonths( dto.getWarrantyMonths() );
        }
    }
}
