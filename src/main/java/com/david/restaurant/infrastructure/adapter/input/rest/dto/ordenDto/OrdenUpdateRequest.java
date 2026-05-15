package com.david.restaurant.infrastructure.adapter.input.rest.dto.ordenDto;

public record OrdenUpdateRequest(
    String tipoOrden,
    Long mesaId,
    String nombreCliente,
    String telefonoCliente,
    String direccionEntrega
) {}
