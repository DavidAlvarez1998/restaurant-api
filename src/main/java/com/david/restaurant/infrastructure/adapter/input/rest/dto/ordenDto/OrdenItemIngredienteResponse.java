package com.david.restaurant.infrastructure.adapter.input.rest.dto.ordenDto;

public record OrdenItemIngredienteResponse(
    Long id,
    Long ingredienteId,
    String ingredienteNombre,
    Double cantidad,
    Double precioUnitario
) {}
