package com.david.restaurant.infrastructure.adapter.input.rest.dto.productoDto;

public record ProductoRequest(
    String nombre,
    String descripcion,
    Double precio,
    String tipo
) {}