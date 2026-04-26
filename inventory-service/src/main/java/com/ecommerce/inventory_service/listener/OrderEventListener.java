package com.ecommerce.inventory_service.listener;

import com.ecommerce.inventory_service.event.OrderCancelledEvent;
import com.ecommerce.inventory_service.event.OrderConfirmedEvent;
import com.ecommerce.inventory_service.event.OrderPlaceEvent;
import com.ecommerce.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final InventoryService inventoryService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "inventory-queue")
    public void handleOrderPlacedEvent(OrderPlaceEvent event){

        log.info("Evento recibido en inventario para la orden: {}", event.orderNumber());


        try {

            boolean allProductsInStock = event
                    .items()
                    .stream()
                    .allMatch(item ->
                    inventoryService
                            .isInStock(item.sku(), item.quantity())
            );

            if(!allProductsInStock){
                cancelOrder(event, "No hay stock suficiente");
                return;
            }

            event.items().forEach(item -> inventoryService.reduceStock(item.sku(), item.quantity()));


            OrderConfirmedEvent confirmedEvent = new OrderConfirmedEvent(event.orderNumber(), event.email());

            rabbitTemplate.convertAndSend("order-events", "order.confirmed", confirmedEvent);
            log.info("Stock descontando para SKU: {}", event.orderNumber());
        } catch (Exception e) {
            log.error("Error inesperado{} : {}", event.orderNumber(),e.getMessage());
            cancelOrder(event, "Error técnico en el procesamiento de inventario");
        }
    }

    private void cancelOrder(OrderPlaceEvent event, String reason){
        OrderCancelledEvent cancelledEvent = new OrderCancelledEvent(
                event.orderNumber(),
                event.email(),
                reason
        );
        log.warn("Error orden cancelada orden: {}", event.orderNumber());
        rabbitTemplate.convertAndSend("order-events", "order.cancelled", cancelledEvent);
    }
}
