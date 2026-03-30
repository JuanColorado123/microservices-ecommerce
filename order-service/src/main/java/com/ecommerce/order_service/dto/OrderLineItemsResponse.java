package com.ecommerce.order_service.dto;

import java.math.BigDecimal;

public record OrderLineItemsResponse(
        Long id,
        String sku,
        BigDecimal price,
        Integer quantity
) {
}
