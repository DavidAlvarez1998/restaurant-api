package com.david.restaurant.infrastructure.adapter.input.rest.dto.ordenDto;

import java.time.LocalDateTime;
import java.util.List;

public record OrdenResponse(
    Long id,
    String tipoOrden,
    Long mesaId,
    String mesaNumero,
    String nombreCliente,
    String telefonoCliente,
    String direccionEntrega,
    LocalDateTime fechaCreacion,
    String estado,
    Double totalMonto,
    List<OrdenItemResponse> items,
    List<PagoResponse> pagos 
) {}
