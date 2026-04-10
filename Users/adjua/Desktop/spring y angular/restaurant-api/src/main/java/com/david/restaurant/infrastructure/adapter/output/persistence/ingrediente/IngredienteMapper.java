package com.david.restaurant.infrastructure.adapter.output.persistence.ingrediente;

import org.springframework.stereotype.Component;

import com.david.restaurant.domain.model.Ingrediente;

@Component
public class IngredienteMapper {
    public IngredienteEntity toEntity(Ingrediente ingrediente){
        if (ingrediente == null) return null;

        IngredienteEntity entity = new IngredienteEntity();
        entity.setId(ingrediente.getId());
        entity.setNombre(ingrediente.getNombre());
        entity.setPrecio(ingrediente.getPrecio());
        return entity;
    }

    public Ingrediente toDomain(IngredienteEntity entity){
        if (entity == null) return null;

        return new Ingrediente(
            entity.getId(),
            entity.getNombre(),
            entity.getPrecio()
        );
    }
}
