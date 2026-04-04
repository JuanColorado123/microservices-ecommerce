package com.ecommerce.order_service.exception;

public class HandleServiceConnectionFailure extends RuntimeException {
    public HandleServiceConnectionFailure(String message) {
        super(message);
    }
}
