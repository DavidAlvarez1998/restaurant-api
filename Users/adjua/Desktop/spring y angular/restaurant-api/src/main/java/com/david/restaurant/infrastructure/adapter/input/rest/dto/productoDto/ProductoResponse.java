package com.david.restaurant.infrastructure.adapter.input.rest.dto.productoDto;


public record ProductoResponse(
    Long id,
    String nombre,
    String descripcion,
    Double precio,
    String tipo
) {}