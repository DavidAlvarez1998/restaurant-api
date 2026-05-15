package com.david.restaurant.infrastructure.adapter.input.rest.controller;

import com.david.restaurant.domain.model.ReporteVentas;
import com.david.restaurant.domain.port.input.OrdenServicePort;
import com.david.restaurant.infrastructure.adapter.input.rest.dto.reporteDto.ReporteVentasResponse;
import com.david.restaurant.infrastructure.adapter.input.rest.mapper.ReporteRestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final OrdenServicePort ordenServicePort;
    private final ReporteRestMapper reporteRestMapper;

        @GetMapping("/ventas")
    public ResponseEntity<ReporteVentasResponse> getVentas(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {
        LocalDate inicio = LocalDate.parse(fechaInicio);
        LocalDate fin = LocalDate.parse(fechaFin);
        ReporteVentas reporte = ordenServicePort.getReporteVentas(inicio, fin);
        return ResponseEntity.ok(reporteRestMapper.toResponse(reporte));
    }

}
