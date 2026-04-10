package com.david.restaurant.infrastructure.adapter.input.rest.dto.ordenDto;

public record PagoRequest(
    String metodoPago,
    Double montoPagado,
    Double propina
) {}
