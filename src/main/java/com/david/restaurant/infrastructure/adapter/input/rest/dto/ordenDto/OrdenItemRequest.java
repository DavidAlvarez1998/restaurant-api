package com.david.restaurant.infrastructure.adapter.input.rest.dto.ordenDto;

import java.util.List;

public record OrdenItemRequest(
    Long productoId,
    Integer cantidad,
    String notas,
    List<OrdenItemIngredienteRequest> ingredientes
) {}
