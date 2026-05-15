package com.david.restaurant.infrastructure.adapter.input.rest.dto.reporteDto;

import java.time.LocalDate;

public record ReporteVentasResponse(
    LocalDate fechaInicio,
    LocalDate fechaFin,
    Double totalVentas,
    Double totalPropinas,
    Double totalEfectivo,
    Double totalTarjeta,
    Double totalTransferencia,
    Long cantidadOrdenes
) {}
