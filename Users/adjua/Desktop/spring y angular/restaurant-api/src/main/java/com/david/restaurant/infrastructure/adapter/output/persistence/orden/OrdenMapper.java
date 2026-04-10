package com.david.restaurant.infrastructure.adapter.output.persistence.orden;

import com.david.restaurant.domain.model.*;
import com.david.restaurant.infrastructure.adapter.output.persistence.mesa.MesaMapper;
import com.david.restaurant.infrastructure.adapter.output.persistence.producto.ProductoMapper;
import com.david.restaurant.infrastructure.adapter.output.persistence.ingrediente.IngredienteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrdenMapper {

    private final MesaMapper mesaMapper;
    private final ProductoMapper productoMapper;
    private final IngredienteMapper ingredienteMapper;

    // --- DE DOMINIO A ENTIDAD (Para guardar) ---
    public OrdenEntity toEntity(Orden domain) {
        if (domain == null) return null;

        OrdenEntity entity = new OrdenEntity();
        entity.setId(domain.getId());
        entity.setTipoOrden(domain.getTipoOrden());
        entity.setNombreCliente(domain.getNombreCliente());
        entity.setTelefonoCliente(domain.getTelefonoCliente());
        entity.setDireccionEntrega(domain.getDireccionEntrega());
        entity.setFechaCreacion(domain.getFechaCreacion());
        entity.setEstado(domain.getEstado());
        entity.setTotalMonto(domain.getTotalMonto());

        if (domain.getMesa() != null) {
            entity.setMesa(mesaMapper.toEntity(domain.getMesa()));
        }

        if (domain.getItems() != null) {
            List<OrdenItemEntity> itemsEntities = domain.getItems().stream()
                .map(itemDomain -> toItemEntity(itemDomain, entity))
                .collect(Collectors.toList());
            entity.setItems(itemsEntities);
        }

        if (domain.getPagos() != null) {
            List<PagoEntity> pagoEntities = domain.getPagos().stream()
                .map(pagoDomain -> toPagoEntity(pagoDomain, entity))
                .collect(Collectors.toList());
            entity.setPagos(pagoEntities);
        }

        return entity;
    }

    private OrdenItemEntity toItemEntity(OrdenItem domainItem, OrdenEntity ordenEntity) {
        if (domainItem == null) return null;
        
        OrdenItemEntity entity = new OrdenItemEntity();
        entity.setId(domainItem.getId());
        entity.setOrden(ordenEntity); // Asignamos el Abuelo
        entity.setProducto(productoMapper.toEntity(domainItem.getProducto()));
        entity.setCantidad(domainItem.getCantidad());
        entity.setPrecioUnitario(domainItem.getPrecioUnitario());
        entity.setNotas(domainItem.getNotas());

        if (domainItem.getIngredientes() != null) {
            List<OrdenItemIngredienteEntity> ingredientesEntities = domainItem.getIngredientes().stream()
                .map(ingDomain -> toIngredienteEntity(ingDomain, entity))
                .collect(Collectors.toList());
            entity.setIngredientes(ingredientesEntities);
        }
        return entity;
    }

    private OrdenItemIngredienteEntity toIngredienteEntity(OrdenItemIngrediente domainIng, OrdenItemEntity itemEntity) {
        if (domainIng == null) return null;
        
        OrdenItemIngredienteEntity entity = new OrdenItemIngredienteEntity();
        entity.setId(domainIng.getId());
        entity.setOrdenItem(itemEntity); // Asignamos el Hijo
        entity.setIngrediente(ingredienteMapper.toEntity(domainIng.getIngrediente()));
        entity.setCantidad(domainIng.getCantidad());
        entity.setPrecioUnitario(domainIng.getPrecioUnitario());
        return entity;
    }

    // --- DE ENTIDAD A DOMINIO (Para responder al Frontend) ---
    public Orden toDomain(OrdenEntity entity) {
        if (entity == null) return null;

        Orden domain = new Orden();
        domain.setId(entity.getId());
        domain.setTipoOrden(entity.getTipoOrden());
        domain.setNombreCliente(entity.getNombreCliente());
        domain.setTelefonoCliente(entity.getTelefonoCliente());
        domain.setDireccionEntrega(entity.getDireccionEntrega());
        domain.setFechaCreacion(entity.getFechaCreacion());
        domain.setEstado(entity.getEstado());
        domain.setTotalMonto(entity.getTotalMonto());

        if (entity.getMesa() != null) {
            domain.setMesa(mesaMapper.toDomain(entity.getMesa()));
        }

        if (entity.getItems() != null) {
            List<OrdenItem> itemsDomain = entity.getItems().stream()
                .map(this::toItemDomain)
                .collect(Collectors.toList());
            domain.setItems(itemsDomain);
        } else {
            domain.setItems(Collections.emptyList());
        }

        if (entity.getPagos() != null) {
            List<Pago> pagosDomain = entity.getPagos().stream()
                .map(this::toPagoDomain)
                .collect(Collectors.toList());
            domain.setPagos(pagosDomain);
        } else {
            domain.setPagos(Collections.emptyList());
        }

        return domain;
    }

    private OrdenItem toItemDomain(OrdenItemEntity entityItem) {
        if (entityItem == null) return null;

        OrdenItem domain = new OrdenItem();
        domain.setId(entityItem.getId());
        domain.setProducto(productoMapper.toDomain(entityItem.getProducto()));
        domain.setCantidad(entityItem.getCantidad());
        domain.setPrecioUnitario(entityItem.getPrecioUnitario());
        domain.setNotas(entityItem.getNotas());

        if (entityItem.getIngredientes() != null) {
            List<OrdenItemIngrediente> ingredientesDomain = entityItem.getIngredientes().stream()
                .map(this::toIngredienteDomain)
                .collect(Collectors.toList());
            domain.setIngredientes(ingredientesDomain);
        } else {
            domain.setIngredientes(Collections.emptyList());
        }
        return domain;
    }

    private OrdenItemIngrediente toIngredienteDomain(OrdenItemIngredienteEntity entityIng) {
        if (entityIng == null) return null;
        
        OrdenItemIngrediente domain = new OrdenItemIngrediente();
        domain.setId(entityIng.getId());
        domain.setIngrediente(ingredienteMapper.toDomain(entityIng.getIngrediente()));
        domain.setCantidad(entityIng.getCantidad());
        domain.setPrecioUnitario(entityIng.getPrecioUnitario());
        return domain;
    }

    private PagoEntity toPagoEntity(Pago domainPago, OrdenEntity ordenEntity) {
        if (domainPago == null) return null;
        PagoEntity entity = new PagoEntity();
        entity.setId(domainPago.getId());
        entity.setOrden(ordenEntity);
        entity.setMontoPagado(domainPago.getMontoPagado());
        entity.setMetodoPago(domainPago.getMetodoPago());
        entity.setPropina(domainPago.getPropina());
        entity.setFechaPago(domainPago.getFechaPago());
        return entity;
    }

    private Pago toPagoDomain(PagoEntity entityPago) {
        if (entityPago == null) return null;
        Pago domain = new Pago();
        domain.setId(entityPago.getId());
        domain.setMontoPagado(entityPago.getMontoPagado());
        domain.setMetodoPago(entityPago.getMetodoPago());
        domain.setPropina(entityPago.getPropina());
        domain.setFechaPago(entityPago.getFechaPago());
        return domain;
    }

}
