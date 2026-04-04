package com.ecommerce.order_service.controller;

import com.ecommerce.order_service.dto.ApiResponse;
import com.ecommerce.order_service.dto.OrderRequest;
import com.ecommerce.order_service.dto.OrderResponse;
import com.ecommerce.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(
            @Valid @RequestBody OrderRequest orderRequest,
            @AuthenticationPrincipal Jwt jtw
    ) {

        OrderResponse order = orderService.placeOrder(orderRequest, jtw.getSubject());

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
    @SuppressWarnings("unchecked")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders(@AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject();
        boolean isAdmin = false;

        Map<String,Object> realmAccess = jwt.getClaim("realm_access");

        if(realmAccess != null  && realmAccess.containsKey("roles")){
            List<String> roles = (List<String>) realmAccess.get("roles");

            isAdmin = roles.stream().anyMatch(role -> role.equals("ADMIN"));
        }

        List<OrderResponse> orders = orderService.getAllOrders(userId,isAdmin);

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