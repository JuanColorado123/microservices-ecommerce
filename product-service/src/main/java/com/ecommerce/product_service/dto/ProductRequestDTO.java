package com.ecommerce.product_service.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductRequestDTO(

        @NotBlank(message = "El campo nombre no puede estar vació")
        @Pattern(
                regexp = "^[a-zA-Z0-9 ]+$",
                message = "El nombre solo puede contener letras, números y espacios"
        )
        String name,

        @NotBlank(message = "El campo descripción no puede estar vació")
        @Size(min = 10, max = 200, message = "El campo descripción debe estar entre 10 y 200 caracteres")
        @Pattern(
                regexp = "^[a-zA-Z0-9 ]+$",
                message = "El nombre solo puede contener letras, números y espacios"
        )
        String description,

        @NotNull(message = "El campo precio no puede estar vacio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El campo precio debe ser mayor que 0")
        @DecimalMax(value = "999999999999999999", message = "El campo precio es demasiado alto")
        BigDecimal price
) {}
