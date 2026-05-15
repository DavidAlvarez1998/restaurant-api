package com.david.restaurant.application.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
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
import com.david.restaurant.domain.model.OrdenDivision;
import com.david.restaurant.domain.model.OrdenEvento;
import com.david.restaurant.domain.model.OrdenItem;
import com.david.restaurant.domain.model.SepararItemResultado;
import com.david.restaurant.domain.model.TipoEventoOrden;
import com.david.restaurant.domain.model.OrdenItemIngrediente;
import com.david.restaurant.domain.model.Pago;
import com.david.restaurant.domain.model.EstadoOrden;
import com.david.restaurant.domain.model.Producto;
import com.david.restaurant.domain.model.Pagina;
import com.david.restaurant.domain.model.ResultadoDivision;
import com.david.restaurant.domain.model.ReporteVentas;
import com.david.restaurant.domain.model.TipoOrden;
import com.david.restaurant.application.event.OrdenTransactionEvent;
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
    private final ApplicationEventPublisher eventPublisher;

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

                double costoIngredientesItem = 0.0;

                // 3. Procesar ingredientes adicionales de cada item
                if (item.getIngredientes() != null) {
                    for (OrdenItemIngrediente ing : item.getIngredientes()) {
                        Ingrediente ingrediente = ingredienteRepositoryPort.findById(ing.getIngrediente().getId())
                            .orElseThrow(() -> new IngredienteNotFoundException(ing.getIngrediente().getId()));

                        ing.setIngrediente(ingrediente);
                        ing.setPrecioUnitario(ingrediente.getPrecio());

                        costoIngredientesItem += ing.getPrecioUnitario() * ing.getCantidad();
                    }
                } else {
                    item.setIngredientes(new ArrayList<>());
                }

                // (precio + extras por unidad) × cantidad
                double subtotalItem = (item.getPrecioUnitario() + costoIngredientesItem) * item.getCantidad();
                totalMonto += subtotalItem;
            }
        }

        // 4. Establecer datos automáticos
        orden.setTotalMonto(totalMonto);
        orden.setFechaCreacion(LocalDateTime.now());
        orden.setEstado(EstadoOrden.ABIERTA);

        // 5. Guardar y notificar
        return saveAndPublish(orden, TipoEventoOrden.CREADA);
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
        Orden orden = findById(id);
        eventPublisher.publishEvent(new OrdenTransactionEvent(this, new OrdenEvento(TipoEventoOrden.ELIMINADA, orden)));
        ordenRepositoryPort.deleteById(id);
    }

    @Override
    @Transactional
    public Orden updateEstado(Long id, String nuevoEstado) {
        return ordenRepositoryPort.findById(id).map(orden -> {
            orden.setEstado(EstadoOrden.valueOf(nuevoEstado));
            return saveAndPublish(orden, TipoEventoOrden.ACTUALIZADA);
        }).orElseThrow(() -> new OrdenNotFoundException(id));
    }

    @Override
    @Transactional
    public Orden addItemToOrden(Long ordenId, OrdenItem nuevoItem) {
        
        Orden orden = findById(ordenId);
        
        if (orden.getEstado() == EstadoOrden.CANCELADA) {
            throw new IllegalStateException("No puedes agregar platos a una orden cancelada.");
        }

        Producto producto = productoRepositoryPort.findById(nuevoItem.getProducto().getId())
            .orElseThrow(() -> new ProductoNotFoundException(nuevoItem.getProducto().getId()));
        
        nuevoItem.setProducto(producto);
        nuevoItem.setPrecioUnitario(producto.getPrecio());
        double costoIngredientesItem = 0.0;

        // 4. Procesar los ingredientes seleccionados para este nuevo plato
        if (nuevoItem.getIngredientes() != null) {
            for (OrdenItemIngrediente ing : nuevoItem.getIngredientes()) {
                Ingrediente ingrediente = ingredienteRepositoryPort.findById(ing.getIngrediente().getId())
                    .orElseThrow(() -> new IngredienteNotFoundException(ing.getIngrediente().getId()));

                ing.setIngrediente(ingrediente);
                ing.setPrecioUnitario(ingrediente.getPrecio());
                costoIngredientesItem += ing.getPrecioUnitario() * ing.getCantidad();
            }
        } else {
            nuevoItem.setIngredientes(new ArrayList<>());
        }

        // 5. Vincular a la orden y sumar la matemática a la factura
        double subtotalItem = (nuevoItem.getPrecioUnitario() + costoIngredientesItem) * nuevoItem.getCantidad();
        orden.getItems().add(nuevoItem);
        orden.setTotalMonto(orden.getTotalMonto() + subtotalItem);
        recalcularPagada(orden);
        return saveAndPublish(orden, TipoEventoOrden.ACTUALIZADA);
    }

    @Override
    @Transactional
    public Orden updateOrdenItem(Long ordenId, Long itemId, OrdenItem itemActualizado) {
        Orden orden = findById(ordenId);

        if (orden.getEstado() == EstadoOrden.CANCELADA) {
            throw new IllegalStateException("No se puede modificar una orden cancelada.");
        }

        OrdenItem item = orden.getItems().stream()
            .filter(i -> i.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("El item con ID " + itemId + " no fue encontrado en esta Orden."));

        // Costo anterior para ajustar el total
        double ingAnterior = 0.0;
        if (item.getIngredientes() != null) {
            for (OrdenItemIngrediente ing : item.getIngredientes()) {
                ingAnterior += ing.getPrecioUnitario() * ing.getCantidad();
            }
        }
        double costoAnterior = (item.getPrecioUnitario() + ingAnterior) * item.getCantidad();

        // Actualizar campos básicos
        item.setCantidad(itemActualizado.getCantidad());
        item.setNotas(itemActualizado.getNotas());

        // Reemplazar ingredientes con precios actualizados
        List<OrdenItemIngrediente> nuevosIngredientes = new ArrayList<>();
        if (itemActualizado.getIngredientes() != null) {
            for (OrdenItemIngrediente ingNuevo : itemActualizado.getIngredientes()) {
                Ingrediente ingrediente = ingredienteRepositoryPort.findById(ingNuevo.getIngrediente().getId())
                    .orElseThrow(() -> new IngredienteNotFoundException(ingNuevo.getIngrediente().getId()));
                ingNuevo.setIngrediente(ingrediente);
                ingNuevo.setPrecioUnitario(ingrediente.getPrecio());
                nuevosIngredientes.add(ingNuevo);
            }
        }
        item.setIngredientes(nuevosIngredientes);

        // Costo nuevo
        double ingNuevo = 0.0;
        for (OrdenItemIngrediente ing : nuevosIngredientes) {
            ingNuevo += ing.getPrecioUnitario() * ing.getCantidad();
        }
        double costoNuevo = (item.getPrecioUnitario() + ingNuevo) * item.getCantidad();

        orden.setTotalMonto(orden.getTotalMonto() - costoAnterior + costoNuevo);
        recalcularPagada(orden);
        return saveAndPublish(orden, TipoEventoOrden.ACTUALIZADA);
    }

    @Override
    @Transactional
    public Orden removerItemDeOrden(Long ordenId, Long itemId) {
        Orden orden = findById(ordenId);

        if (orden.isPagada() || orden.getEstado() == EstadoOrden.CANCELADA) {
            throw new IllegalStateException("No se puede modificar una orden pagada o cancelada.");
        }

        // Buscar el plato específico dentro de la lista de la orden
        OrdenItem itemARemover = orden.getItems().stream()
            .filter(item -> item.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("El plato con ID " + itemId + " no fue encontrado en esta Orden."));

        // Matemáticas: Calcular cuánto costaba exactamente este plato con todos sus extras
        double costoIngPorUnidad = 0.0;
        if (itemARemover.getIngredientes() != null) {
            for (OrdenItemIngrediente ing : itemARemover.getIngredientes()) {
                costoIngPorUnidad += ing.getPrecioUnitario() * ing.getCantidad();
            }
        }

        // Restar ese dinero al Gran Total de la orden
        double montoADescontar = (itemARemover.getPrecioUnitario() + costoIngPorUnidad) * itemARemover.getCantidad();
        orden.setTotalMonto(orden.getTotalMonto() - montoADescontar);

        // Remover físicamente el ítem (JPA lo eliminará de la tabla cuando guardemos la orden)
        orden.getItems().remove(itemARemover);
        return saveAndPublish(orden, TipoEventoOrden.ACTUALIZADA);
    }

    @Override
    @Transactional
    public Orden pagarOrden(Long ordenId, Pago nuevoPago) {
        Orden orden = findById(ordenId);
        
        // 1. Validar que la orden esté lista para pagarse
        if (orden.isPagada() || orden.getEstado() == EstadoOrden.CANCELADA) {
            throw new IllegalStateException("Esta orden ya está pagada o fue cancelada.");
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

        // 4. Si la plata entregada ya cubre o supera el costo total de la orden, marcar como pagada
        if (totalPagadoHastaAhora >= orden.getTotalMonto()) {
            orden.setPagada(true);
        }
        return saveAndPublish(orden, TipoEventoOrden.ACTUALIZADA);
    }

    @Override
    @Transactional(readOnly = true)
    public Pagina<Orden> findHistorialPaginado(int page, int size) {
        return ordenRepositoryPort.findHistorialPaginado(page, size);
    }

    @Override
    @Transactional
    public Orden updateOrden(Long id, Orden datos) {
        Orden orden = findById(id);

        orden.setTipoOrden(datos.getTipoOrden());
        orden.setNombreCliente(datos.getNombreCliente());
        orden.setTelefonoCliente(datos.getTelefonoCliente());
        orden.setDireccionEntrega(datos.getDireccionEntrega());

        if (datos.getTipoOrden() == TipoOrden.MESA) {
            if (datos.getMesa() == null || datos.getMesa().getId() == null) {
                throw new MesaNotFoundException(0L);
            }
            Mesa mesa = mesaRepositoryPort.findById(datos.getMesa().getId())
                .orElseThrow(() -> new MesaNotFoundException(datos.getMesa().getId()));
            orden.setMesa(mesa);
        } else {
            orden.setMesa(null);
        }

        return saveAndPublish(orden, TipoEventoOrden.ACTUALIZADA);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Orden> findOrdenesPendientes() {
        return findAll().stream()
            .filter(o -> o.getEstado() == EstadoOrden.ABIERTA
                      || o.getEstado() == EstadoOrden.EN_PREPARACION
                      || o.getEstado() == EstadoOrden.LISTA
                      || o.getEstado() == EstadoOrden.EN_CAMINO)
            .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Orden> findOrdenesFinalizadas() {
        return findAll().stream()
            .filter(o -> o.getEstado() == EstadoOrden.ENTREGADA
                      || o.getEstado() == EstadoOrden.CANCELADA
                      || o.getEstado() == EstadoOrden.PAGADA) // legacy
            .sorted((a, b) -> b.getFechaCreacion().compareTo(a.getFechaCreacion()))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteVentas getReporteVentas(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Orden> ordenesPagadas = findAll().stream()
            .filter(o -> o.isPagada() || o.getEstado() == EstadoOrden.PAGADA) // incluye legacy
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

    @Override
    @Transactional
    public ResultadoDivision dividirOrden(Long ordenId, List<OrdenDivision> itemsDivision) {
        Orden original = findById(ordenId);

        if (original.getEstado() == EstadoOrden.CANCELADA || original.isPagada()) {
            throw new IllegalStateException("No se puede dividir una orden pagada o cancelada.");
        }

        // Build map itemId → cantidad a dividir
        java.util.Map<Long, Integer> divisionMap = itemsDivision.stream()
            .collect(java.util.stream.Collectors.toMap(OrdenDivision::itemId, OrdenDivision::cantidad));

        // Validate quantities and ensure at least one unit stays
        int totalUnidadesOriginales = original.getItems().stream().mapToInt(OrdenItem::getCantidad).sum();
        int totalUnidadesASplit = 0;
        for (java.util.Map.Entry<Long, Integer> entry : divisionMap.entrySet()) {
            OrdenItem item = original.getItems().stream()
                .filter(i -> i.getId().equals(entry.getKey()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("El item " + entry.getKey() + " no existe en esta orden."));
            if (entry.getValue() <= 0 || entry.getValue() > item.getCantidad()) {
                throw new IllegalArgumentException("Cantidad inválida para el item " + entry.getKey());
            }
            totalUnidadesASplit += entry.getValue();
        }
        if (totalUnidadesASplit >= totalUnidadesOriginales) {
            throw new IllegalStateException("No se puede mover la totalidad de la orden. Deja al menos un producto.");
        }

        // Process each item in original
        List<OrdenItem> nuevosItems = new ArrayList<>();
        List<OrdenItem> itemsAEliminar = new ArrayList<>();
        double montoASplit = 0.0;

        for (OrdenItem item : original.getItems()) {
            Integer cantidadASplit = divisionMap.get(item.getId());
            if (cantidadASplit == null || cantidadASplit <= 0) continue;

            double ingPorUnidad = 0.0;
            if (item.getIngredientes() != null) {
                for (OrdenItemIngrediente ing : item.getIngredientes()) {
                    ingPorUnidad += ing.getPrecioUnitario() * ing.getCantidad();
                }
            }
            montoASplit += (item.getPrecioUnitario() + ingPorUnidad) * cantidadASplit;

            int restante = item.getCantidad() - cantidadASplit;
            if (restante == 0) {
                itemsAEliminar.add(item);
            } else {
                item.setCantidad(restante);
            }

            // Build new item (no id → INSERT)
            OrdenItem nuevoItem = new OrdenItem();
            nuevoItem.setProducto(item.getProducto());
            nuevoItem.setCantidad(cantidadASplit);
            nuevoItem.setPrecioUnitario(item.getPrecioUnitario());
            nuevoItem.setNotas(item.getNotas());

            List<OrdenItemIngrediente> nuevosIngs = new ArrayList<>();
            if (item.getIngredientes() != null) {
                for (OrdenItemIngrediente ing : item.getIngredientes()) {
                    OrdenItemIngrediente nuevoIng = new OrdenItemIngrediente();
                    nuevoIng.setIngrediente(ing.getIngrediente());
                    nuevoIng.setCantidad(ing.getCantidad());
                    nuevoIng.setPrecioUnitario(ing.getPrecioUnitario());
                    nuevosIngs.add(nuevoIng);
                }
            }
            nuevoItem.setIngredientes(nuevosIngs);
            nuevosItems.add(nuevoItem);
        }

        // Apply removals and update original total
        java.util.Set<Long> idsAEliminar = itemsAEliminar.stream()
            .map(OrdenItem::getId)
            .collect(java.util.stream.Collectors.toSet());
        original.getItems().removeIf(i -> idsAEliminar.contains(i.getId()));
        original.setTotalMonto(original.getTotalMonto() - montoASplit);
        recalcularPagada(original);
        Orden originalGuardado = saveAndPublish(original, TipoEventoOrden.ACTUALIZADA);

        // Create new order
        Orden nueva = new Orden();
        nueva.setTipoOrden(original.getTipoOrden());
        nueva.setMesa(original.getMesa());
        nueva.setNombreCliente(original.getNombreCliente());
        nueva.setTelefonoCliente(original.getTelefonoCliente());
        nueva.setDireccionEntrega(original.getDireccionEntrega());
        nueva.setFechaCreacion(LocalDateTime.now());
        nueva.setEstado(EstadoOrden.ABIERTA);
        nueva.setPagada(false);
        nueva.setItems(nuevosItems);
        nueva.setTotalMonto(montoASplit);
        nueva.setPagos(new ArrayList<>());

        Orden nuevaGuardada = saveAndPublish(nueva, TipoEventoOrden.CREADA);
        return new ResultadoDivision(originalGuardado, nuevaGuardada);
    }

    @Override
    @Transactional
    public SepararItemResultado separarItem(Long ordenId, Long itemId) {
        Orden orden = findById(ordenId);

        if (orden.getEstado() == EstadoOrden.CANCELADA) {
            throw new IllegalStateException("No se puede modificar una orden cancelada.");
        }

        OrdenItem item = orden.getItems().stream()
            .filter(i -> i.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("El item con ID " + itemId + " no fue encontrado."));

        // Si ya tiene cantidad 1, no hace falta separar — devolvemos el mismo item
        if (item.getCantidad() <= 1) {
            return new SepararItemResultado(orden, itemId);
        }

        // Capturamos los IDs existentes para identificar el nuevo después del save
        java.util.Set<Long> idsAntes = orden.getItems().stream()
            .filter(i -> i.getId() != null)
            .map(OrdenItem::getId)
            .collect(java.util.stream.Collectors.toSet());

        // Reducir el item original en 1
        item.setCantidad(item.getCantidad() - 1);

        // Clonar como nuevo item con cantidad=1 (id=null → INSERT)
        OrdenItem nuevoItem = new OrdenItem();
        nuevoItem.setProducto(item.getProducto());
        nuevoItem.setCantidad(1);
        nuevoItem.setPrecioUnitario(item.getPrecioUnitario());
        nuevoItem.setNotas(item.getNotas());

        List<OrdenItemIngrediente> nuevosIngs = new ArrayList<>();
        if (item.getIngredientes() != null) {
            for (OrdenItemIngrediente ing : item.getIngredientes()) {
                OrdenItemIngrediente nuevoIng = new OrdenItemIngrediente();
                nuevoIng.setIngrediente(ing.getIngrediente());
                nuevoIng.setCantidad(ing.getCantidad());
                nuevoIng.setPrecioUnitario(ing.getPrecioUnitario());
                nuevosIngs.add(nuevoIng);
            }
        }
        nuevoItem.setIngredientes(nuevosIngs);
        orden.getItems().add(nuevoItem);
        // totalMonto no cambia: mismo costo, solo redistribuido entre dos items

        Orden savedOrden = saveAndPublish(orden, TipoEventoOrden.ACTUALIZADA);

        // El nuevo item es el que tiene un ID que no existía antes del save
        Long nuevoItemId = savedOrden.getItems().stream()
            .map(OrdenItem::getId)
            .filter(id -> !idsAntes.contains(id))
            .findFirst()
            .orElse(itemId);

        return new SepararItemResultado(savedOrden, nuevoItemId);
    }

    private Orden saveAndPublish(Orden orden, TipoEventoOrden tipo) {
        Orden saved = ordenRepositoryPort.save(orden);
        eventPublisher.publishEvent(new OrdenTransactionEvent(this, new OrdenEvento(tipo, saved)));
        return saved;
    }

    private void recalcularPagada(Orden orden) {
        double totalPagado = orden.getPagos() == null ? 0.0
            : orden.getPagos().stream().mapToDouble(Pago::getMontoPagado).sum();
        orden.setPagada(totalPagado >= orden.getTotalMonto());
    }

}
