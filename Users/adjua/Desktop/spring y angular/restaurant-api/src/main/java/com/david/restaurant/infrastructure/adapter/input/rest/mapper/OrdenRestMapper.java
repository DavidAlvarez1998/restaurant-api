package com.david.restaurant.infrastructure.adapter.input.rest.mapper;

import com.david.restaurant.domain.model.*;
import com.david.restaurant.infrastructure.adapter.input.rest.dto.ordenDto.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrdenRestMapper {

    // --- De DTO (Request) a Dominio ---
    public Orden toDomain(OrdenRequest request) {
        if (request == null) return null;

        Orden orden = new Orden();
        if (request.tipoOrden() != null) {
            orden.setTipoOrden(TipoOrden.valueOf(request.tipoOrden()));
        }
        
        if (request.mesaId() != null) {
            Mesa mesa = new Mesa();
            mesa.setId(request.mesaId());
            orden.setMesa(mesa); // Solo pasamos el ID, el servicio buscará el resto
        }

        orden.setNombreCliente(request.nombreCliente());
        orden.setTelefonoCliente(request.telefonoCliente());
        orden.setDireccionEntrega(request.direccionEntrega());

        if (request.items() != null) {
            List<OrdenItem> itemsDomain = request.items().stream()
                .map(this::toItemDomain)
                .collect(Collectors.toList());
            orden.setItems(itemsDomain);
        }

        return orden;
    }

    public  OrdenItem toItemDomain(OrdenItemRequest requestItem) {
        if (requestItem == null) return null;

        OrdenItem item = new OrdenItem();
        
        Producto producto = new Producto();
        producto.setId(requestItem.productoId());
        item.setProducto(producto); // Solo pasamos el ID

        item.setCantidad(requestItem.cantidad());
        item.setNotas(requestItem.notas());

        if (requestItem.ingredientes() != null) {
            List<OrdenItemIngrediente> ingredientesDomain = requestItem.ingredientes().stream()
                .map(this::toIngredienteDomain)
                .collect(Collectors.toList());
            item.setIngredientes(ingredientesDomain);
        } else {
            item.setIngredientes(Collections.emptyList());
        }

        return item;
    }

    private OrdenItemIngrediente toIngredienteDomain(OrdenItemIngredienteRequest requestIng) {
        if (requestIng == null) return null;

        OrdenItemIngrediente ingredienteAdicional = new OrdenItemIngrediente();
        
        Ingrediente ingrediente = new Ingrediente();
        ingrediente.setId(requestIng.ingredienteId());
        ingredienteAdicional.setIngrediente(ingrediente); // Solo el ID

        ingredienteAdicional.setCantidad(requestIng.cantidad());
        return ingredienteAdicional;
    }

    // --- De Dominio a DTO (Response) ---
    public OrdenResponse toResponse(Orden orden) {
        if (orden == null) return null;

        return new OrdenResponse(
            orden.getId(),
            orden.getTipoOrden() != null ? orden.getTipoOrden().name() : null,
            orden.getMesa() != null ? orden.getMesa().getId() : null,
            orden.getMesa() != null ? orden.getMesa().getNumero() : null,
            orden.getNombreCliente(),
            orden.getTelefonoCliente(),
            orden.getDireccionEntrega(),
            orden.getFechaCreacion(),
            orden.getEstado() != null ? orden.getEstado().name() : null,
            orden.getTotalMonto(),
            orden.getItems() != null ? orden.getItems().stream().map(this::toItemResponse).collect(Collectors.toList()) : Collections.emptyList(),
            orden.getPagos() != null ? orden.getPagos().stream().map(this::toPagoResponse).collect(Collectors.toList()) : Collections.emptyList()
        );
    }

    private OrdenItemResponse toItemResponse(OrdenItem item) {
        if (item == null) return null;

        return new OrdenItemResponse(
            item.getId(),
            item.getProducto() != null ? item.getProducto().getId() : null,
            item.getProducto() != null ? item.getProducto().getNombre() : null,
            item.getCantidad(),
            item.getPrecioUnitario(),
            item.getNotas(),
            item.getIngredientes() != null ? item.getIngredientes().stream().map(this::toIngredienteResponse).collect(Collectors.toList()) : Collections.emptyList()
        );
    }

    private OrdenItemIngredienteResponse toIngredienteResponse(OrdenItemIngrediente ingredienteAdicional) {
        if (ingredienteAdicional == null) return null;

        return new OrdenItemIngredienteResponse(
            ingredienteAdicional.getId(),
            ingredienteAdicional.getIngrediente() != null ? ingredienteAdicional.getIngrediente().getId() : null,
            ingredienteAdicional.getIngrediente() != null ? ingredienteAdicional.getIngrediente().getNombre() : null,
            ingredienteAdicional.getCantidad(),
            ingredienteAdicional.getPrecioUnitario()
        );
    }

    private PagoResponse toPagoResponse(Pago pago) {
        if (pago == null) return null;
        return new PagoResponse(
            pago.getId(),
            pago.getMontoPagado(),
            pago.getMetodoPago() != null ? pago.getMetodoPago().name() : null,
            pago.getPropina(),
            pago.getFechaPago()
        );
    }

    public Pago toPagoDomain(PagoRequest request) {
        if (request == null) return null;
        Pago pago = new Pago();
        pago.setMetodoPago(MetodoPago.valueOf(request.metodoPago()));
        pago.setMontoPagado(request.montoPagado());
        pago.setPropina(request.propina() != null ? request.propina() : 0.0);
        return pago;
    }

    public OrdenCocinaResponse toCocinaResponse(Orden orden) {
        if (orden == null) return null;

        List<OrdenCocinaItemResponse> itemsCocina = orden.getItems() != null
            ? orden.getItems().stream().map(item -> new OrdenCocinaItemResponse(
                item.getId(),
                item.getProducto() != null ? item.getProducto().getNombre() : null,
                item.getCantidad(),
                item.getNotas(),
                item.getIngredientes() != null
                    ? item.getIngredientes().stream()
                        .map(ing -> ing.getIngrediente() != null ? ing.getIngrediente().getNombre() : null)
                        .collect(Collectors.toList())
                    : Collections.emptyList()
            )).collect(Collectors.toList())
            : Collections.emptyList();

        return new OrdenCocinaResponse(
            orden.getId(),
            orden.getTipoOrden() != null ? orden.getTipoOrden().name() : null,
            orden.getMesa() != null ? orden.getMesa().getNumero() : null,
            orden.getNombreCliente(),
            orden.getFechaCreacion(),
            orden.getEstado() != null ? orden.getEstado().name() : null,
            itemsCocina
        );
        }


}
