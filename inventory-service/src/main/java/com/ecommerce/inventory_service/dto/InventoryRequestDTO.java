package com.ecommerce.inventory_service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record InventoryRequestDTO(
        @NotEmpty(message = "El campo SKU no puede estar vació")
        @Pattern(
                regexp = "^[a-zA-Z0-9 _ - -]+$",
                message = "El campo SKU solo puede contener letras, números y espacios"
        )
        String sku,

        @NotNull(message = "El campo cantidad no puede estar vació")
        @Positive(message = "El campo cantidad debe ser positivo")
        Integer quantity
) {}
