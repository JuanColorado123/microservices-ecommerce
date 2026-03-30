package com.ecommerce.product_service.service;

import com.ecommerce.product_service.dto.ProductRequestDTO;
import com.ecommerce.product_service.dto.ProductResponseDTO;

import java.util.List;

public interface ProductService {
    ProductResponseDTO getProductById(String id);
    List<ProductResponseDTO> getAllProducts();
    ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO);
    ProductResponseDTO updateProduct(String id, ProductRequestDTO productRequestDTO);
    void deleteProductById(String id);
}
