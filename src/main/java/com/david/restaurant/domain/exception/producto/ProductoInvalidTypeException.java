package com.david.restaurant.domain.exception.producto;

public class ProductoInvalidTypeException extends RuntimeException {
    public ProductoInvalidTypeException(String message) {
        super(message);
    }
}