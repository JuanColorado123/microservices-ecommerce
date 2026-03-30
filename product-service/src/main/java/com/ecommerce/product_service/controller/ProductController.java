package com.ecommerce.product_service.controller;

import com.ecommerce.product_service.dto.ApiResponse;
import com.ecommerce.product_service.dto.ProductRequestDTO;
import com.ecommerce.product_service.dto.ProductResponseDTO;
import com.ecommerce.product_service.model.Product;
import com.ecommerce.product_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> getProductById(@PathVariable String id){

        ProductResponseDTO productResponseDTO = productService.getProductById(id);

        return new ResponseEntity<>(
                new ApiResponse<>(
                        true,
                        "Producto recuperado correctamente",
                        200,
                        productResponseDTO,
                        LocalDateTime.now()
                ), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> getAllProducts(){

        List<ProductResponseDTO> responseDTOList = productService.getAllProducts();

        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Productos recuperados correctamente",
                200,
                responseDTOList,
                LocalDateTime.now()
        ),HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDTO>> createProduct(@Valid @RequestBody ProductRequestDTO productResponseDTO){

        ProductResponseDTO savedProduct = productService.createProduct(productResponseDTO);

        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Producto creado correcdtamente",
                201,
                savedProduct,
                LocalDateTime.now()
        ),HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> updateProdct(@PathVariable String id,
                                                                        @Valid @RequestBody ProductRequestDTO productRequestDTO){

        ProductResponseDTO productResponseDTO = productService.updateProduct(id, productRequestDTO);

        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Producto actualizado correctamente",
                200,
                productResponseDTO,
                LocalDateTime.now()
        ),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteProductById(@PathVariable String id){

        productService.deleteProductById(id);

        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Producto eliminado correctamente",
                200,
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
}
