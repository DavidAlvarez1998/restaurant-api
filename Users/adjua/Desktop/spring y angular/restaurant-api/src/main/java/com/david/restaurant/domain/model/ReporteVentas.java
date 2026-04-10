package com.david.restaurant.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReporteVentas {
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Double totalVentas;
    private Double totalPropinas;
    private Double totalEfectivo;
    private Double totalTarjeta;
    private Double totalTransferencia;
    private Long cantidadOrdenes;
}
