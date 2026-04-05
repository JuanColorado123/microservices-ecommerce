package com.ecommerce.inventory_service.service.impl;

import com.ecommerce.inventory_service.dto.InventoryRequestDTO;
import com.ecommerce.inventory_service.dto.InventoryResponseDTO;
import com.ecommerce.inventory_service.exceptions.ResourceNotFoundException;
import com.ecommerce.inventory_service.mapper.InventoryMapper;
import com.ecommerce.inventory_service.model.Inventory;
import com.ecommerce.inventory_service.repository.InventoryRepository;
import com.ecommerce.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Log4j2
@Service
@RequiredArgsConstructor
@RefreshScope
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

    @Value("${inventory.allow-backorders:false}")
    private boolean allowBackorders;

    @Override
    @Transactional(readOnly = true)
    public boolean isInStock(String sku, Integer quantity) {

        if(allowBackorders){
            log.warn("MODO BACKORDER ACTIVO: Autoriza stock para SKU: {}", sku);
            return true;
        }

        Inventory inventory = inventoryRepository
                .findBySku(sku)
                .orElseThrow(() ->
                        new ResourceNotFoundException("No se encontro el inventario con SKU: " + sku)
                );

        return inventory.getQuantity() >= quantity;
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponseDTO getInventoryById(Long id) {

        log.info("Buscando inventory por ID:{}", id);

        return inventoryMapper.toInventoryResponseDTO(inventoryRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("No se encontró el inventario con ID: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponseDTO getInventoryBySku(String sku) {

        log.info("Buscando inventory por SKU:{}", sku);

        Inventory inventory = inventoryRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Error al buscar el inventario por sku: " + sku));

        log.info("Se encontró inventory por SKU:{}, cantidad:{}", sku, inventory.getQuantity());

        InventoryResponseDTO inventoryResponseDTO = inventoryMapper.toInventoryResponseDTO(inventory);

        log.info("Se mapeo correctamente el inventario con SKU: {}", sku);

        return inventoryResponseDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponseDTO> getAllInventories() {

        log.info("Buscando todos los inventory por");

        List<Inventory> inventories = inventoryRepository.findAll();

        String inventoriesIds = inventories
                .stream()
                .map(inventory -> inventory.getId().toString())
                .collect(Collectors.joining(", "));

        log.info("Inventarios encontrados IDs {}", inventoriesIds);

        return inventoryMapper.toListInventoryResponseDTO(inventories);
    }

    @Override
    @Transactional()
    public InventoryResponseDTO createInventory(InventoryRequestDTO inventoryRequestDTO) {
        log.info("Creando inventario SKU: {}", inventoryRequestDTO.sku());

        Inventory inventory = inventoryMapper.toInventory(inventoryRequestDTO);

        InventoryResponseDTO inventoryResponseDTO = inventoryMapper.toInventoryResponseDTO(inventoryRepository.save(inventory));

        log.info("Creado inventario SKU: {}", inventoryResponseDTO.sku());

        return inventoryResponseDTO;
    }

    @Override
    @Transactional()
    public InventoryResponseDTO updateInventory(Long id, InventoryRequestDTO inventoryRequestDTO) {

        Inventory inventory = inventoryRepository
                .findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("No se encontró el inventario con ID: " + id));

        inventoryMapper.toInventoryUpdate(inventoryRequestDTO, inventory);

        return inventoryMapper.toInventoryResponseDTO(inventoryRepository.save(inventory));
    }

    @Override
    public void deleteInventoryById(Long id) {

        if(!inventoryRepository.existsById(id)){
            throw new ResourceNotFoundException("No se encontró el inventario con el ID: " + id);
        }

        inventoryRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void reduceStock(String sku, Integer quantity) {

        var inventory = inventoryRepository.findBySku(sku)
                .orElseThrow(() ->
                        new ResourceNotFoundException("No se encontró el inventario con el SKU: " + sku)
                );

        if (inventory.getQuantity() <  quantity){
            throw new RuntimeException("Stock insuficiente para: " + sku);
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventoryRepository.save(inventory);

        log.info("stock actualizado correctamente: " + sku);
    }
}
