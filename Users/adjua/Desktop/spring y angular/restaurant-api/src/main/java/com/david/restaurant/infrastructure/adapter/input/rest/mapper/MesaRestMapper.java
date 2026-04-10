package com.david.restaurant.infrastructure.adapter.input.rest.mapper;

import com.david.restaurant.domain.model.Mesa;
import com.david.restaurant.infrastructure.adapter.input.rest.dto.MesaDto.MesaRequest;
import com.david.restaurant.infrastructure.adapter.input.rest.dto.MesaDto.MesaResponse;

import org.springframework.stereotype.Component;

@Component
public class MesaRestMapper {


    public Mesa toDomain(MesaRequest request) {
        if (request == null) {
            return null;
        }
        Mesa mesa = new Mesa();
        mesa.setNumero(request.numero()); 
        return mesa;
    }

    public MesaResponse toResponse(Mesa mesa) {
        if (mesa == null) {
            return null;
        }
        return new MesaResponse(
            mesa.getId(),
            mesa.getNumero()
        );
    }
}