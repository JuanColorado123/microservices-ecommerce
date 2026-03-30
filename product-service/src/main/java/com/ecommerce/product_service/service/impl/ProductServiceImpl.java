package com.ecommerce.product_service.service.impl;

import com.ecommerce.product_service.dto.ProductRequestDTO;
import com.ecommerce.product_service.dto.ProductResponseDTO;
import com.ecommerce.product_service.exceptions.ResourceNotFoundException;
import com.ecommerce.product_service.mapper.ProductMapper;
import com.ecommerce.product_service.model.Product;
import com.ecommerce.product_service.repository.ProductRepository;
import com.ecommerce.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponseDTO getProductById(String id) {

        log.info("Buscando producto con id: {}", id);

        Product product = productRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.error("Producto no encontrado con id: {}", id);
                    return new ResourceNotFoundException("No se encontró el ID: " + id);
                });

        log.info("Producto encontrado: {}", product.getName());

        return productMapper.toProductResponseDTO(product);
    }

    @Override
    public List<ProductResponseDTO> getAllProducts() {

        log.info("Obteniendo lista de productos");

        List<Product> productList = productRepository.findAll();

        log.info("Cantidad de productos encontrados: {}", productList.size());

        String names = productList.stream()
                .map(Product::getName)
                .collect(Collectors.joining(", "));

        log.info("Productos listados: {}", names);

        return productMapper.toProductListResponseDto(productList);
    }

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {

        log.info("Creando producto: {}", productRequestDTO.name());

        Product product = productMapper.toProduct(productRequestDTO);

        Product savedProduct = productRepository.save(product);

        log.info("Producto creado con id: {}", savedProduct.getId());

        return productMapper.toProductResponseDTO(savedProduct);
    }

    @Override
    public ProductResponseDTO updateProduct(String id, ProductRequestDTO productRequestDTO) {

        log.info("Actualizando producto con id: {}", id);

        Product product = productRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.error("No se puede actualizar, producto no encontrado con id: {}", id);
                    return new ResourceNotFoundException("No se encontró el ID: " + id);
                });

        productMapper.updateProductByResponseDto(productRequestDTO, product);

        Product updatedProduct = productRepository.save(product);

        log.info("Producto actualizado con id: {}", updatedProduct.getId());

        return productMapper.toProductResponseDTO(updatedProduct);
    }

    @Override
    public void deleteProductById(String id) {

        log.info("Eliminando producto con id: {}", id);

        if (!productRepository.existsById(id)) {
            log.error("No se puede eliminar, producto no encontrado con id: {}", id);
            throw new ResourceNotFoundException("No se encontró el ID: " + id);
        }

        productRepository.deleteById(id);

        log.info("Producto eliminado con id: {}", id);
    }
}