package com.ecommerce.order_service.controller;

import com.ecommerce.order_service.dto.ApiResponse;
import com.ecommerce.order_service.dto.OrderRequest;
import com.ecommerce.order_service.dto.OrderResponse;
import com.ecommerce.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(
            @Valid @RequestBody OrderRequest orderRequest) {

        OrderResponse order = orderService.placeOrder(orderRequest);

        ApiResponse<OrderResponse> response = new ApiResponse<>(
                true,
                "Orden creada correctamente",
                HttpStatus.CREATED.value(),
                order,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {

        List<OrderResponse> orders = orderService.getAllOrders();

        ApiResponse<List<OrderResponse>> response = new ApiResponse<>(
                true,
                "Lista de órdenes",
                HttpStatus.OK.value(),
                orders,
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long id) {

        OrderResponse order = orderService.getOrderById(id);

        ApiResponse<OrderResponse> response = new ApiResponse<>(
                true,
                "Orden encontrada",
                HttpStatus.OK.value(),
                order,
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteOrder(@PathVariable Long id) {

        orderService.deleteOrder(id);

        ApiResponse<Object> response = new ApiResponse<>(
                true,
                "Orden eliminada correctamente",
                HttpStatus.OK.value(),
                null,
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }
}