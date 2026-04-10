package com.david.restaurant.infrastructure.adapter.input.rest.dto.ordenDto;

import java.util.List;

public record OrdenRequest(
    String tipoOrden,
    Long mesaId,
    String nombreCliente,
    String telefonoCliente,
    String direccionEntrega,
    List<OrdenItemRequest> items
) {}
