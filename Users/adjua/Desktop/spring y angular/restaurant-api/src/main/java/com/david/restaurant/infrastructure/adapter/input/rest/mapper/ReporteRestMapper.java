package com.david.restaurant.infrastructure.adapter.input.rest.mapper;

import com.david.restaurant.domain.model.ReporteVentas;
import com.david.restaurant.infrastructure.adapter.input.rest.dto.reporteDto.ReporteVentasResponse;
import org.springframework.stereotype.Component;

@Component
public class ReporteRestMapper {

    public ReporteVentasResponse toResponse(ReporteVentas domain) {
        if (domain == null) return null;
        return new ReporteVentasResponse(
            domain.getFechaInicio(),
            domain.getFechaFin(),
            domain.getTotalVentas(),
            domain.getTotalPropinas(),
            domain.getTotalEfectivo(),
            domain.getTotalTarjeta(),
            domain.getTotalTransferencia(),
            domain.getCantidadOrdenes()
        );
    }
}
