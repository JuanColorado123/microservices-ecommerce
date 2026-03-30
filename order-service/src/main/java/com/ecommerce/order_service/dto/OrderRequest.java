package com.ecommerce.order_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OrderRequest(
        @NotEmpty(message = "La orden debe contener al menos un item")
        @Valid
        List<OrderLineItemsRequest> orderLineItemsList
) {
}
