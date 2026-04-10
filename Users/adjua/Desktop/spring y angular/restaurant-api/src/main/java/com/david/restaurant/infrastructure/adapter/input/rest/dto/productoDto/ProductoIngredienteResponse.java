package com.david.restaurant.infrastructure.adapter.input.rest.dto.productoDto;

public record ProductoIngredienteResponse(
    Long id,
    Long ingredienteId,
    String ingredienteNombre,
    Double cantidad
) {}
