package com.david.restaurant.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.david.restaurant.domain.exception.mesa.MesaAlreadyExistsException;
import com.david.restaurant.domain.exception.mesa.MesaNotFoundException;
import com.david.restaurant.domain.model.Mesa;
import com.david.restaurant.domain.port.input.MesaServicePort;
import com.david.restaurant.domain.port.output.MesaRepositoryPort;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class MesaService implements MesaServicePort{

    private final MesaRepositoryPort mesaRepositoryPort;

    @Transactional
    public Mesa saveMesa(Mesa mesa) {
        if (mesaRepositoryPort.existsByNumero(mesa.getNumero())) {
            throw new MesaAlreadyExistsException(mesa.getNumero());
        }
        return mesaRepositoryPort.save(mesa);
    }
    
    @Transactional
    (readOnly = true)
    public List<Mesa> findAll() {
        return mesaRepositoryPort.findAll();
    }

    @Transactional
    (readOnly = true)
    public Mesa findById(Long id) {
        return mesaRepositoryPort.findById(id)
        .orElseThrow(() -> new MesaNotFoundException(id)); 
    }

    @Transactional
    public void deleteById(Long id) {
        if (!mesaRepositoryPort.existsById(id)) {
            throw new MesaNotFoundException(id);
        }
        mesaRepositoryPort.deleteById(id);
    }

    @Transactional
    public Mesa updateMesa(Long id, Mesa mesaUpdate) {
        return mesaRepositoryPort.findById(id).map(mesaBD -> {
            if (mesaUpdate.getNumero() != null) {
                mesaBD.setNumero(mesaUpdate.getNumero());
            }
            return mesaRepositoryPort.save(mesaBD);
        }).orElseThrow(() -> new MesaNotFoundException(id)); 
    }

    
}