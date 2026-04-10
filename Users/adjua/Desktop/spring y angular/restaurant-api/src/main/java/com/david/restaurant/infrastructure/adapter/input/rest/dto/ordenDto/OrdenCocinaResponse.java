package com.david.restaurant.infrastructure.adapter.input.rest.dto.ordenDto;

import java.time.LocalDateTime;
import java.util.List;

public record OrdenCocinaResponse(
    Long ordenId,
    String tipoOrden,
    String mesaNumero,
    String nombreCliente,
    LocalDateTime fechaCreacion,
    String estado,
    List<OrdenCocinaItemResponse> items
) {}
