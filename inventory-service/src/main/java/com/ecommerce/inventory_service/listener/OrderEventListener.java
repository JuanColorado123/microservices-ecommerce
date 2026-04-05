package com.ecommerce.inventory_service.listener;

import com.ecommerce.inventory_service.event.OrderPlaceEvent;
import com.ecommerce.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final InventoryService inventoryService;


    @RabbitListener(queues = "inventory-queue")
    public void HandlerOrderPlacedEvent(OrderPlaceEvent event){

        log.info("Evento recibido en inventario para la orden: {}", event.orderNumber());

        event.items().forEach(item ->{

            try {
                inventoryService.reduceStock(item.sku(), item.quantity());
                log.info("Stock descontando para SKU: {}", item.sku());
            } catch (Exception e) {
                log.error("Error al descontar el stock SKU {} : {}", item.sku(),e.getMessage());
            }

        });
    }
}
