package com.david.restaurant.domain.port.input;

import com.david.restaurant.domain.model.Orden;
import com.david.restaurant.domain.model.OrdenDivision;
import com.david.restaurant.domain.model.Pagina;
import com.david.restaurant.domain.model.OrdenItem;
import com.david.restaurant.domain.model.Pago;
import com.david.restaurant.domain.model.ResultadoDivision;
import com.david.restaurant.domain.model.ReporteVentas;
import com.david.restaurant.domain.model.SepararItemResultado;

import java.time.LocalDate;
import java.util.List;

public interface OrdenServicePort {
    Orden createOrden(Orden orden);
    List<Orden> findAll();
    Orden findById(Long id);
    void deleteById(Long id);
    Orden updateEstado(Long id, String nuevoEstado);
    Orden addItemToOrden(Long ordenId, OrdenItem nuevoItem);
    Orden updateOrdenItem(Long ordenId, Long itemId, OrdenItem itemActualizado);
    Orden removerItemDeOrden(Long ordenId, Long itemId);
    Orden pagarOrden(Long ordenId, Pago nuevoPago);
    Orden updateOrden(Long id, Orden datos);
    Pagina<Orden> findHistorialPaginado(int page, int size);
    ResultadoDivision dividirOrden(Long ordenId, List<OrdenDivision> items);
    SepararItemResultado separarItem(Long ordenId, Long itemId);
    List<Orden> findOrdenesPendientes();
    List<Orden> findOrdenesFinalizadas();
    ReporteVentas getReporteVentas(LocalDate fechaInicio, LocalDate fechaFin);


}
