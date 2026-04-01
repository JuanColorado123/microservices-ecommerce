package com.ecommerce.inventory_service.controller;

import com.ecommerce.inventory_service.dto.ApiResponse;
import com.ecommerce.inventory_service.dto.InventoryRequestDTO;
import com.ecommerce.inventory_service.dto.InventoryResponseDTO;
import com.ecommerce.inventory_service.service.InventoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;


    @GetMapping({"/in-stock/{sku}"})
    @ResponseStatus(HttpStatus.OK)
    public Boolean isInventoryInStock(@PathVariable("sku") String sku, @RequestParam Integer quantity){

        return inventoryService.isInStock(sku,quantity);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InventoryResponseDTO>> getInventoryById(@PathVariable Long id){

        InventoryResponseDTO responseDTO = inventoryService.getInventoryById(id);

        ApiResponse<InventoryResponseDTO> apiResponse = new ApiResponse<>(
                true,
                "Inventario recuperado correctamente",
                200,
                responseDTO,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InventoryResponseDTO>>> getAllInvetories(HttpServletRequest request){

        log.info("Petición atendida desde el puerto: {}", request.getServerPort() );

        List<InventoryResponseDTO> inventoryResponseDTOList = inventoryService.getAllInventories();

        ApiResponse<List<InventoryResponseDTO>> apiResponse = new ApiResponse<>(
                true,
                "Lista de inventarios recuperado correctamente",
                200,
                inventoryResponseDTOList,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<ApiResponse<InventoryResponseDTO>> createInventory(@Valid @RequestBody InventoryRequestDTO inventoryRequestDTO){

        InventoryResponseDTO savedInventory = inventoryService.createInventory(inventoryRequestDTO);

        ApiResponse<InventoryResponseDTO> apiResponse = new ApiResponse<>(
                true,
                "Inventario creado correctamente",
                201,
                savedInventory,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InventoryResponseDTO>> updateInventory(@PathVariable Long id,
                                                                             @Valid @RequestBody InventoryRequestDTO inventoryRequestDTO){
        InventoryResponseDTO updatedInventory = inventoryService.updateInventory(id,inventoryRequestDTO);

        ApiResponse<InventoryResponseDTO> apiResponse = new ApiResponse<>(
                true,
                "Inventario creado correctamente",
                201,
                updatedInventory,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @PutMapping("/reduce/{sku}")
    public ResponseEntity<ApiResponse<InventoryResponseDTO>> updateInventory(@PathVariable String sku,
                                                                             @RequestParam Integer quantity){

        inventoryService.reduceStock(sku, quantity);

        ApiResponse<InventoryResponseDTO> apiResponse = new ApiResponse<>(
                true,
                "Stock reducido",
                200,
                null,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteInventoryById(@PathVariable Long id){

        inventoryService.deleteInventoryById(id);

        ApiResponse<Object> apiResponse = new ApiResponse<>(
                true,
                "Inventario eliminado correctamente",
                200,
                null,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(apiResponse, HttpStatus.OK) ;
    }
}