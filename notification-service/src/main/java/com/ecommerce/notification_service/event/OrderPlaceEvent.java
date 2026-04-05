package com.ecommerce.notification_service.event;

import java.util.List;

public record OrderPlaceEvent(String orderNumber, String email, List<OrderLineItemEvent> items){

    public record OrderLineItemEvent(String sku, String price,Integer quantity){

    }
}
