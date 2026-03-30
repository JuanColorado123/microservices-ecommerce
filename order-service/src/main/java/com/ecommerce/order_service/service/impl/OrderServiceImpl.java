package com.ecommerce.order_service.service.impl;

import com.ecommerce.order_service.dto.OrderRequest;
import com.ecommerce.order_service.dto.OrderResponse;
import com.ecommerce.order_service.exception.ResourceNotFoundException;
import com.ecommerce.order_service.mapper.OrderMapper;
import com.ecommerce.order_service.model.Order;
import com.ecommerce.order_service.model.OrderLineItems;
import com.ecommerce.order_service.repository.OrderRepository;
import com.ecommerce.order_service.service.OrderService;
import com.ecommerce.order_service.service.client.InventoryClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

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
    public OrderResponse placeOrder(OrderRequest orderRequest) {

        if (!ordersEnable){
            log.warn("Pedido  rechazado: Servicio deshabilitado por configuración");
            throw new RuntimeException("Pedido  rechazado: Servicio deshabilitado por configuración");
        }
        log.info("Colocando nueva orden...");

        Order order = orderMapper.toOrder(orderRequest);

        for (var item: order.getOrderLineItemsList()){


            String sku = item.getSku();
            Integer quantity = item.getQuantity();

            try {

                log.info("Iniciando llamado al servicio inventory-service para el sku {}, con cantidad de : {}",sku,quantity);

                inventoryClient.reduceStock(sku, quantity);

                log.info("Finalizando llamado al servicio inventory-service para el sku {}, con cantidad de : {}",sku,quantity);
            }catch (Exception ex){

                log.error("Error llamando al servicio inventory-service para reducir stock para el producto: {}, {}", sku, ex.getMessage());

                throw new IllegalArgumentException("No se pudo procesar la orden:Stock insuficiente o error de inventario");
            }
        }
        order.setOrderNumber(UUID.randomUUID().toString());

        Order savedOrder = orderRepository.save(order);
        log.info("Orden guardada con éxito. ID: {}", savedOrder.getId());

        return orderMapper.toOrderResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
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