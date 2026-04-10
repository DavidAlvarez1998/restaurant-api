package com.david.restaurant.infrastructure.adapter.input.rest.mapper;

import org.springframework.stereotype.Component;

import com.david.restaurant.domain.model.Ingrediente;
import com.david.restaurant.infrastructure.adapter.input.rest.dto.ingredienteDto.IngredienteRequest;
import com.david.restaurant.infrastructure.adapter.input.rest.dto.ingredienteDto.IngredienteResponse;

@Component
public class IngredienteRestMapper {
    
    public Ingrediente toDomain(IngredienteRequest request){
        if (request == null) return null;
        Ingrediente ingrediente = new Ingrediente();
        ingrediente.setNombre(request.nombre());
        ingrediente.setPrecio(request.precio());
        return ingrediente;
    }

    public IngredienteResponse toResponse(Ingrediente ingrediente){
        if (ingrediente == null) return null;
        return new IngredienteResponse(
            ingrediente.getId(),
            ingrediente.getNombre(),
            ingrediente.getPrecio()
        );
    }

}
