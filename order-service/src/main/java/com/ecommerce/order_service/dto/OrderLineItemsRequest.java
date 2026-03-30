package com.ecommerce.order_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OrderLineItemsRequest(
        @NotBlank(message = "El SKU es obligatorio")
        String sku,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal price,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1)
        Integer quantity
){}
