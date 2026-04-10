package com.david.restaurant.infrastructure.adapter.input.rest.dto.ordenDto;

import java.util.List;

public record OrdenCocinaItemResponse(
    Long itemId,
    String nombreProducto,
    Integer cantidad,
    String notas,
    List<String> ingredientesExtra
) {}
