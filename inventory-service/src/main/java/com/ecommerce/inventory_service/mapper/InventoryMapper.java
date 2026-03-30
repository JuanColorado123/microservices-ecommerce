package com.ecommerce.inventory_service.mapper;

import com.ecommerce.inventory_service.dto.InventoryRequestDTO;
import com.ecommerce.inventory_service.dto.InventoryResponseDTO;
import com.ecommerce.inventory_service.model.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(target = "inStock" ,expression = "java(inventory.getQuantity() > 0)")
    InventoryResponseDTO toInventoryResponseDTO(Inventory inventory);

    @Mapping(target = "inStock" ,expression = "java(inventory.getQuantity() > 0) ")
    List<InventoryResponseDTO> toListInventoryResponseDTO(List<Inventory> inventories);

    @Mapping(target = "id" ,ignore = true)
    Inventory toInventory(InventoryRequestDTO inventoryRequestDTO);

    void toInventoryUpdate(InventoryRequestDTO dto, @MappingTarget Inventory inventory);
}
