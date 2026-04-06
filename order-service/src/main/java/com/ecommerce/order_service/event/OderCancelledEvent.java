package com.ecommerce.order_service.event;

public record OderCancelledEvent(String orderNumber, String email, String reason) {
}
