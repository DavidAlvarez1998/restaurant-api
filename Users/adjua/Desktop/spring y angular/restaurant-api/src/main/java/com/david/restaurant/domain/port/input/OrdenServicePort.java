package com.david.restaurant.domain.port.input;

import com.david.restaurant.domain.model.Orden;
import com.david.restaurant.domain.model.OrdenItem;
import com.david.restaurant.domain.model.Pago;
import com.david.restaurant.domain.model.ReporteVentas;

import java.time.LocalDate;
import java.util.List;

public interface OrdenServicePort {
    Orden createOrden(Orden orden);
    List<Orden> findAll();
    Orden findById(Long id);
    void deleteById(Long id);
    Orden updateEstado(Long id, String nuevoEstado);
    Orden addItemToOrden(Long ordenId, OrdenItem nuevoItem);
    Orden removerItemDeOrden(Long ordenId, Long itemId);
    Orden pagarOrden(Long ordenId, Pago nuevoPago);
    List<Orden> findOrdenesPendientes();
    List<Orden> findOrdenesFinalizadas();
    ReporteVentas getReporteVentas(LocalDate fechaInicio, LocalDate fechaFin);


}
