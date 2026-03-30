package com.ecommerce.product_service.mapper;

import com.ecommerce.product_service.dto.ProductRequestDTO;
import com.ecommerce.product_service.dto.ProductResponseDTO;
import com.ecommerce.product_service.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    Product toProduct(ProductRequestDTO requestDTO);

    ProductResponseDTO toProductResponseDTO(Product product);

    List<ProductResponseDTO> toProductListResponseDto(List<Product> product);

    @Mapping(target = "id", ignore = true)
    void updateProductByResponseDto(ProductRequestDTO productRequestDTO,@MappingTarget Product product);
}
