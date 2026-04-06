package com.ecommerce.order_service.listener;

import com.ecommerce.order_service.event.OrderPlaceEvent;
import com.ecommerce.order_service.model.OrderStatus;
import com.ecommerce.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderEventListener {

    private final OrderService orderService;

    @RabbitListener(queues = "order-confirmed-queue")
    public void handlerOrderConfirmed(OrderPlaceEvent event){
        orderService.updateOrderByOderNumber(OrderStatus.CONFIRMED, event.orderNumber());
    }

    @RabbitListener(queues = "order-cancelled-queue")
    public void handlerOrderCancelled(OrderPlaceEvent event){
        orderService.updateOrderByOderNumber(OrderStatus.CANCELLED, event.orderNumber());
    }
}
