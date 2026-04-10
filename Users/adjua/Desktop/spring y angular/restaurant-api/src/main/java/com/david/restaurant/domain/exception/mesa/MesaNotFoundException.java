package com.david.restaurant.domain.exception.mesa;

public class MesaNotFoundException extends RuntimeException {
    public MesaNotFoundException(Long id) {
        super("No se encontró ninguna mesa con el ID: " + id);
    }
}