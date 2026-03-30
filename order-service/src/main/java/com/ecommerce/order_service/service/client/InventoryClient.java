package com.ecommerce.order_service.service.client;


import com.ecommerce.order_service.dto.ApiResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.PutExchange;

public interface InventoryClient {

    @PutExchange("/api/v1/inventories/reduce/{sku}")
    ApiResponse<Object> reduceStock(@PathVariable String sku, @RequestParam Integer quantity);
}
