package com.david.restaurant.infrastructure.adapter.input.rest.dto.ordenDto;

import java.time.LocalDateTime;

public record PagoResponse(
    Long id,
    Double montoPagado,
    String metodoPago,
    Double propina,
    LocalDateTime fechaPago
) {}
