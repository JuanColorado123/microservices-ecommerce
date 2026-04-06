package com.ecommerce.order_service.dto;

import java.util.List;

public record OrderResponse(
        Long id,
        String userId,
        String orderNumber,
        String status,
        List<OrderLineItemsResponse> orderLineItemsList
) {
}
