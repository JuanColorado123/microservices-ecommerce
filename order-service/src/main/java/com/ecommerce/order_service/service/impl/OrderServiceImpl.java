package com.ecommerce.order_service.service.impl;

import com.ecommerce.order_service.dto.OrderRequest;
import com.ecommerce.order_service.dto.OrderResponse;
import com.ecommerce.order_service.exception.HandleServiceConnectionFailure;
import com.ecommerce.order_service.exception.ResourceNotFoundException;
import com.ecommerce.order_service.mapper.OrderMapper;
import com.ecommerce.order_service.model.Order;
import com.ecommerce.order_service.repository.OrderRepository;
import com.ecommerce.order_service.service.OrderService;
import com.ecommerce.order_service.service.client.InventoryClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@RefreshScope
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final InventoryClient inventoryClient;

    @Value("${order.enable:true}")
    private boolean ordersEnable;

    @Override
    @Transactional
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    @Retry(name = "inventory")
    public OrderResponse placeOrder(OrderRequest orderRequest, String userId) {

        if (!ordersEnable){
            log.warn("Pedido  rechazado: Servicio deshabilitado por configuración");
            throw new HandleServiceConnectionFailure("Pedido  rechazado: Servicio deshabilitado por configuración");
        }
        log.info("Colocando nueva orden...");

        Order order = orderMapper.toOrder(orderRequest);
        order.setUserId(userId);

        for (var item: order.getOrderLineItemsList()){


            String sku = item.getSku();
            Integer quantity = item.getQuantity();

            try {

                log.info("Iniciando llamado al servicio inventory-service para el sku {}, con cantidad de : {}", sku, quantity);

                inventoryClient.reduceStock(sku, quantity);

                log.info("Finalizando llamado al servicio inventory-service para el sku {}, con cantidad de : {}", sku, quantity);
            } catch (WebClientResponseException.BadRequest ex) {

                throw new IllegalArgumentException("Stock insuficiente para el producto: " + sku);
            }
        }
        order.setOrderNumber(UUID.randomUUID().toString());

        Order savedOrder = orderRepository.save(order);
        log.info("Orden guardada con éxito. ID: {}", savedOrder.getId());

        return orderMapper.toOrderResponse(savedOrder);
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

    public OrderResponse fallbackMethod(OrderRequest orderRequest, String userId, Throwable throwable){
        log.error("Circuit Breaker activado. Causa: {}", throwable.getMessage());

        throw new HandleServiceConnectionFailure("Inventory service no disponible");
    }
}