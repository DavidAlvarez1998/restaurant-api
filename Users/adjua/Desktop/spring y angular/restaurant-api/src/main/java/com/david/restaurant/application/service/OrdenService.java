package com.david.restaurant.application.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.david.restaurant.domain.exception.ingrediente.IngredienteNotFoundException;
import com.david.restaurant.domain.exception.mesa.MesaNotFoundException;
import com.david.restaurant.domain.exception.orden.OrdenNotFoundException;
import com.david.restaurant.domain.exception.producto.ProductoNotFoundException;
import com.david.restaurant.domain.model.Ingrediente;
import com.david.restaurant.domain.model.Mesa;
import com.david.restaurant.domain.model.MetodoPago;
import com.david.restaurant.domain.model.Orden;
import com.david.restaurant.domain.model.OrdenItem;
import com.david.restaurant.domain.model.OrdenItemIngrediente;
import com.david.restaurant.domain.model.Pago;
import com.david.restaurant.domain.model.EstadoOrden;
import com.david.restaurant.domain.model.Producto;
import com.david.restaurant.domain.model.ReporteVentas;
import com.david.restaurant.domain.model.TipoOrden;
import com.david.restaurant.domain.port.input.OrdenServicePort;
import com.david.restaurant.domain.port.output.IngredienteRepositoryPort;
import com.david.restaurant.domain.port.output.MesaRepositoryPort;
import com.david.restaurant.domain.port.output.OrdenRepositoryPort;
import com.david.restaurant.domain.port.output.ProductoRepositoryPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrdenService implements OrdenServicePort {

    private final OrdenRepositoryPort ordenRepositoryPort;
    private final MesaRepositoryPort mesaRepositoryPort;
    private final ProductoRepositoryPort productoRepositoryPort;
    private final IngredienteRepositoryPort ingredienteRepositoryPort;

    @Override
    @Transactional
    public Orden createOrden(Orden orden) {
        // 1. Si es tipo MESA, validar que la mesa exista
        if (orden.getTipoOrden() == TipoOrden.MESA) {
            if (orden.getMesa() == null || orden.getMesa().getId() == null) {
                throw new MesaNotFoundException(0L);
            }
            Mesa mesa = mesaRepositoryPort.findById(orden.getMesa().getId())
                .orElseThrow(() -> new MesaNotFoundException(orden.getMesa().getId()));
            orden.setMesa(mesa);
        }

        // 2. Procesar cada item: buscar producto y calcular precios
        double totalMonto = 0.0;

        if (orden.getItems() != null) {
            for (OrdenItem item : orden.getItems()) {
                // Buscar el producto en el catálogo
                Producto producto = productoRepositoryPort.findById(item.getProducto().getId())
                    .orElseThrow(() -> new ProductoNotFoundException(item.getProducto().getId()));

                item.setProducto(producto);
                item.setPrecioUnitario(producto.getPrecio());

                // Subtotal del item (precio producto * cantidad)
                double subtotalItem = item.getPrecioUnitario() * item.getCantidad();

                // 3. Procesar ingredientes adicionales de cada item
                if (item.getIngredientes() != null) {
                    for (OrdenItemIngrediente ing : item.getIngredientes()) {
                        Ingrediente ingrediente = ingredienteRepositoryPort.findById(ing.getIngrediente().getId())
                            .orElseThrow(() -> new IngredienteNotFoundException(ing.getIngrediente().getId()));

                        ing.setIngrediente(ingrediente);
                        ing.setPrecioUnitario(ingrediente.getPrecio());

                        // Sumar costo del ingrediente adicional
                        subtotalItem += ing.getPrecioUnitario() * ing.getCantidad();
                    }
                } else {
                    item.setIngredientes(new ArrayList<>());
                }

                totalMonto += subtotalItem;
            }
        }

        // 4. Establecer datos automáticos
        orden.setTotalMonto(totalMonto);
        orden.setFechaCreacion(LocalDateTime.now());
        orden.setEstado(EstadoOrden.ABIERTA);

        // 5. Guardar
        return ordenRepositoryPort.save(orden);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Orden> findAll() {
        return ordenRepositoryPort.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Orden findById(Long id) {
        return ordenRepositoryPort.findById(id)
            .orElseThrow(() -> new OrdenNotFoundException(id));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!ordenRepositoryPort.existsById(id)) {
            throw new OrdenNotFoundException(id);
        }
        ordenRepositoryPort.deleteById(id);
    }

    @Override
    @Transactional
    public Orden updateEstado(Long id, String nuevoEstado) {
        return ordenRepositoryPort.findById(id).map(orden -> {
            orden.setEstado(EstadoOrden.valueOf(nuevoEstado));
            return ordenRepositoryPort.save(orden);
        }).orElseThrow(() -> new OrdenNotFoundException(id));
    }

    @Override
    @Transactional
    public Orden addItemToOrden(Long ordenId, OrdenItem nuevoItem) {
        
        Orden orden = findById(ordenId);
        
        if (orden.getEstado() == EstadoOrden.PAGADA || orden.getEstado() == EstadoOrden.CANCELADA) {
            throw new IllegalStateException("No puedes agregar platos a una orden que ya está " + orden.getEstado().name());
        }

        Producto producto = productoRepositoryPort.findById(nuevoItem.getProducto().getId())
            .orElseThrow(() -> new ProductoNotFoundException(nuevoItem.getProducto().getId()));
        
        nuevoItem.setProducto(producto);
        nuevoItem.setPrecioUnitario(producto.getPrecio());
        double subtotalItem = nuevoItem.getPrecioUnitario() * nuevoItem.getCantidad();

        // 4. Procesar los ingredientes seleccionados para este nuevo plato
        if (nuevoItem.getIngredientes() != null) {
            for (OrdenItemIngrediente ing : nuevoItem.getIngredientes()) {
                Ingrediente ingrediente = ingredienteRepositoryPort.findById(ing.getIngrediente().getId())
                    .orElseThrow(() -> new IngredienteNotFoundException(ing.getIngrediente().getId()));
                
                ing.setIngrediente(ingrediente);
                ing.setPrecioUnitario(ingrediente.getPrecio());
                subtotalItem += ing.getPrecioUnitario() * ing.getCantidad();
            }
        } else {
            nuevoItem.setIngredientes(new ArrayList<>());
        }

        // 5. Vincular a la orden y sumar la matemática a la factura
        orden.getItems().add(nuevoItem);
        orden.setTotalMonto(orden.getTotalMonto() + subtotalItem);

        // 6. Guardar todo
        return ordenRepositoryPort.save(orden);
    }

    @Override
    @Transactional
    public Orden removerItemDeOrden(Long ordenId, Long itemId) {
        Orden orden = findById(ordenId);

        if (orden.getEstado() == EstadoOrden.PAGADA || orden.getEstado() == EstadoOrden.CANCELADA) {
            throw new IllegalStateException("No se puede modificar una orden que está " + orden.getEstado().name());
        }

        // Buscar el plato específico dentro de la lista de la orden
        OrdenItem itemARemover = orden.getItems().stream()
            .filter(item -> item.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("El plato con ID " + itemId + " no fue encontrado en esta Orden."));

        // Matemáticas: Calcular cuánto costaba exactamente este plato con todos sus extras
        double costoPlatoBase = itemARemover.getPrecioUnitario() * itemARemover.getCantidad();
        double costoIngredientes = 0.0;
        
        if (itemARemover.getIngredientes() != null) {
            for (OrdenItemIngrediente ing : itemARemover.getIngredientes()) {
                costoIngredientes += ing.getPrecioUnitario() * ing.getCantidad();
            }
        }
        
        // Restar ese dinero al Gran Total de la orden
        double montoADescontar = costoPlatoBase + costoIngredientes;
        orden.setTotalMonto(orden.getTotalMonto() - montoADescontar);

        // Remover físicamente el ítem (JPA lo eliminará de la tabla cuando guardemos la orden)
        orden.getItems().remove(itemARemover);

        return ordenRepositoryPort.save(orden);
    }

    @Override
    @Transactional
    public Orden pagarOrden(Long ordenId, Pago nuevoPago) {
        Orden orden = findById(ordenId);
        
        // 1. Validar que la orden esté lista para pagarse
        if (orden.getEstado() == EstadoOrden.PAGADA || orden.getEstado() == EstadoOrden.CANCELADA) {
            throw new IllegalStateException("Esta orden ya está cerrada o fue cancelada.");
        }

        // 2. Procesar el pago: El monto, método y propina ya vienen del controlador.
        // Solo necesitamos ponerle la fecha de hoy.
        nuevoPago.setFechaPago(java.time.LocalDateTime.now());

        // Asegurarnos de que inicializamos la cartera de pagos si es nula
        if (orden.getPagos() == null) {
            orden.setPagos(new java.util.ArrayList<>());
        }
        
        orden.getPagos().add(nuevoPago);

        // 3. MATEMÁTICAS: Sumar todo lo que el cliente nos ha pagado hasta ahora (Tickets)
        double totalPagadoHastaAhora = 0.0;
        for (Pago p : orden.getPagos()) {
            totalPagadoHastaAhora += p.getMontoPagado();
        }

        // 4. Si la plata entregada ya cubre o supera el costo total de la orden, cambiar a PAGADA
        if (totalPagadoHastaAhora >= orden.getTotalMonto()) {
            orden.setEstado(EstadoOrden.PAGADA);
        }
        
        return ordenRepositoryPort.save(orden);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Orden> findOrdenesPendientes() {
        return findAll().stream()
            .filter(o -> o.getEstado() == EstadoOrden.ABIERTA 
                      || o.getEstado() == EstadoOrden.EN_PREPARACION)
            .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Orden> findOrdenesFinalizadas() {
        return findAll().stream()
            .filter(o -> o.getEstado() == EstadoOrden.PAGADA
                      || o.getEstado() == EstadoOrden.CANCELADA)
            .sorted((a, b) -> b.getFechaCreacion().compareTo(a.getFechaCreacion()))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteVentas getReporteVentas(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Orden> ordenesPagadas = findAll().stream()
            .filter(o -> o.getEstado() == EstadoOrden.PAGADA)
            .filter(o -> {
                LocalDate fechaOrden = o.getFechaCreacion().toLocalDate();
                return !fechaOrden.isBefore(fechaInicio) && !fechaOrden.isAfter(fechaFin);
            })
            .collect(Collectors.toList());

        double totalVentas = 0.0;
        double totalPropinas = 0.0;
        double totalEfectivo = 0.0;
        double totalTarjeta = 0.0;
        double totalTransferencia = 0.0;

        for (Orden orden : ordenesPagadas) {
            if (orden.getPagos() != null) {
                for (Pago pago : orden.getPagos()) {
                    totalVentas += pago.getMontoPagado();
                    totalPropinas += pago.getPropina() != null ? pago.getPropina() : 0.0;

                    if (pago.getMetodoPago() == MetodoPago.EFECTIVO) {
                        totalEfectivo += pago.getMontoPagado();
                    } else if (pago.getMetodoPago() == MetodoPago.TARJETA) {
                        totalTarjeta += pago.getMontoPagado();
                    } else if (pago.getMetodoPago() == MetodoPago.TRANSFERENCIA) {
                        totalTransferencia += pago.getMontoPagado();
                    }
                }
            }
        }

        return new ReporteVentas(
            fechaInicio,
            fechaFin,
            totalVentas,
            totalPropinas,
            totalEfectivo,
            totalTarjeta,
            totalTransferencia,
            (long) ordenesPagadas.size()
        );
    }




}
