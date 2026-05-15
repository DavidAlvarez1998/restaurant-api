package com.david.restaurant.domain.exception.producto;

public class ProductoAlreadyExistsException extends RuntimeException {
    public ProductoAlreadyExistsException(String nombre) {
        super("Ya existe un producto con el nombre: " + nombre);
    }
}