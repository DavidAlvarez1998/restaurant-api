package com.david.restaurant.domain.exception.orden;

public class OrdenNotFoundException extends RuntimeException {
    public OrdenNotFoundException(Long id) {
        super("No se encontró ninguna orden con el ID: " + id);
    }
}
