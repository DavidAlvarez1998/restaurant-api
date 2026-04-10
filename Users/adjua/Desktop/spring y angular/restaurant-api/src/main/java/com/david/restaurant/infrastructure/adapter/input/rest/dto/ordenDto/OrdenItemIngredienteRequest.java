package com.david.restaurant.infrastructure.adapter.input.rest.dto.ordenDto;

public record OrdenItemIngredienteRequest(
    Long ingredienteId,
    Double cantidad
) {}
