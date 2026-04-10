package com.david.restaurant.infrastructure.adapter.input.rest.dto.ordenDto;

import java.util.List;

public record OrdenItemResponse(
    Long id,
    Long productoId,
    String productoNombre,
    Integer cantidad,
    Double precioUnitario,
    String notas,
    List<OrdenItemIngredienteResponse> ingredientes
) {}
