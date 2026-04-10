package com.david.restaurant.infrastructure.adapter.output.persistence.producto;

import com.david.restaurant.domain.model.Producto;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    public ProductoEntity toEntity(Producto domain) {
        if (domain == null) return null;

        ProductoEntity entity = new ProductoEntity();
        entity.setId(domain.getId());
        entity.setNombre(domain.getNombre());
        entity.setDescripcion(domain.getDescripcion());
        entity.setPrecio(domain.getPrecio());
        entity.setTipo(domain.getTipo());
        
        return entity;
    }

    public Producto toDomain(ProductoEntity entity) {
        if (entity == null) return null;

        Producto domain = new Producto();
        domain.setId(entity.getId());
        domain.setNombre(entity.getNombre());
        domain.setDescripcion(entity.getDescripcion());
        domain.setPrecio(entity.getPrecio());
        domain.setTipo(entity.getTipo());
        
        return domain;
    }
}
