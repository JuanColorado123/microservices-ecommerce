package com.ecommerce.inventory_service.service;

import com.ecommerce.inventory_service.dto.InventoryRequestDTO;
import com.ecommerce.inventory_service.dto.InventoryResponseDTO;

import java.util.List;

public interface InventoryService {
    boolean isInStock(String sku, Integer quantity);
    InventoryResponseDTO getInventoryById(Long id);
    InventoryResponseDTO getInventoryBySku(String sku);
    List<InventoryResponseDTO> getAllInventories();
    InventoryResponseDTO createInventory(InventoryRequestDTO inventoryRequestDTO);
    InventoryResponseDTO updateInventory(Long id, InventoryRequestDTO inventoryRequestDTO);
    void deleteInventoryById(Long id);
    void reduceStock(String sku, Integer integer);
}
