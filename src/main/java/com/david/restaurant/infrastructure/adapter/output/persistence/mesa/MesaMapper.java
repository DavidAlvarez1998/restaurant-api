package com.david.restaurant.infrastructure.adapter.output.persistence.mesa;

import org.springframework.stereotype.Component;

import com.david.restaurant.domain.model.Mesa;

@Component
public class MesaMapper {

    // Convierte de Dominio a entity
    public MesaEntity toEntity(Mesa mesa) {
        if (mesa == null) return null;
        
        MesaEntity entity = new MesaEntity();
        entity.setId(mesa.getId());
        entity.setNumero(mesa.getNumero());
        return entity;
    }

    // Convierte de entity a Dominio
    public Mesa toDomain(MesaEntity entity) {
        if (entity == null) return null;
        
        return new Mesa(
            entity.getId(),
            entity.getNumero()
        );
    }
    
}