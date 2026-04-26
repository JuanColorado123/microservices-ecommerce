package com.ecommerce.order_service.listener;

import com.ecommerce.order_service.event.OderCancelledEvent;
import com.ecommerce.order_service.event.OrderConfirmedEvent;
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
    public void handlerOrderConfirmed(OrderConfirmedEvent event){
        if(event.orderNumber() == null){
            log.info("OrderConfirmedEvent con orderNumber null");
            return;
        }
        orderService.updateOrderByOderNumber(OrderStatus.CONFIRMED, event.orderNumber());
    }

    @RabbitListener(queues = "order-cancelled-queue")
    public void handlerOrderCancelled(OderCancelledEvent event){
        if(event.orderNumber() == null){
            log.info("OderCancelledEvent con orderNumber null");
            return;
        }
        orderService.updateOrderByOderNumber(OrderStatus.CANCELLED, event.orderNumber());
    }
}
