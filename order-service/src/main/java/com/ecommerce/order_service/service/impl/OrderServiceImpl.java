package com.ecommerce.order_service.service.impl;

import com.ecommerce.order_service.dto.OrderRequest;
import com.ecommerce.order_service.dto.OrderResponse;
import com.ecommerce.order_service.event.OrderPlaceEvent;
import com.ecommerce.order_service.exception.HandleServiceConnectionFailure;
import com.ecommerce.order_service.exception.ResourceNotFoundException;
import com.ecommerce.order_service.mapper.OrderMapper;
import com.ecommerce.order_service.model.Order;
import com.ecommerce.order_service.model.OrderStatus;
import com.ecommerce.order_service.repository.OrderRepository;
import com.ecommerce.order_service.service.OrderService;
import com.ecommerce.order_service.service.OutBoxEventsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@RefreshScope
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final RabbitTemplate rabbitTemplate;
    private final OutBoxEventsService outBoxEventsService;

    @Value("${order.enable:true}")
    private boolean ordersEnable;

    @Override
    @Transactional
    public OrderResponse placeOrder(OrderRequest orderRequest, String userId) {

        if (!ordersEnable){
            log.warn("Pedido  rechazado: Servicio deshabilitado por configuración");
            throw new HandleServiceConnectionFailure("Pedido  rechazado: Servicio deshabilitado por configuración");
        }

        log.info("Colocando nueva orden...");

        Order order = orderMapper.toOrder(orderRequest);

        order.setUserId(userId);

        order.setOrderNumber(UUID.randomUUID().toString());
        order.setStatus(OrderStatus.PLACED);
        Order savedOrder = orderRepository.save(order);
        log.info("Orden guardada con éxito. ID: {}", savedOrder.getId());

        List<OrderPlaceEvent.OrderLineItemEvent> orderLineItem = order
                .getOrderLineItemsList()
                .stream()
                .map(item -> new OrderPlaceEvent
                        .OrderLineItemEvent(
                                item.getSku(),
                                item.getPrice().toString(),
                                item.getQuantity()))
                .toList();

        OrderPlaceEvent orderPlaceEvent = new OrderPlaceEvent(savedOrder.getOrderNumber(), orderRequest.email(), orderLineItem);

        boolean sendToRabbit = false;

        try {
            rabbitTemplate.convertAndSend("order-events", "order.placed", orderPlaceEvent);
            sendToRabbit = true;
            log.error("Llamado con exitoso RabbitMQ enviando: {}", savedOrder.getId());

        } catch (AmqpException e) {
            log.error("Llamado fallido a RabbitMQ, no se encuentra en servicio: {}", savedOrder.getId());
        }

        outBoxEventsService.saveOrderPlacedEvent(orderPlaceEvent, sendToRabbit);

        log.info("Evento enviado a RabbitMQ para su procesamiento. ID: {}", savedOrder.getId());

        return orderMapper.toOrderResponse(savedOrder);
    }

    @Override
    @Transactional
    public void updateOrderByOderNumber(OrderStatus orderStatus,String orderNumber) {

       Order order = orderRepository
                .findByOrderNumber(orderNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException("No se encontró la order con el numero: " + orderNumber )
                );

       order.setStatus(orderStatus);
       orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders(String userId, boolean isAdmin) {

        List<Order> orders;

        if (isAdmin){
            orders = orderRepository.findAll();
        }else{
            orders = orderRepository.findByUserId(userId);
        }

        return orders.stream()
                .map(orderMapper::toOrderResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("no se encontró Orden con ID:" + id));
        return orderMapper.toOrderResponse(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("no se encontró Orden con ID:" + id);
        }
        orderRepository.deleteById(id);
        log.info("Orden eliminada. ID: {}", id);
    }
}