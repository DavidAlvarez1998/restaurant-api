package com.david.restaurant.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pago {
    private Long id;
    private Double montoPagado;
    private MetodoPago metodoPago;
    private Double propina;
    private LocalDateTime fechaPago;
}
