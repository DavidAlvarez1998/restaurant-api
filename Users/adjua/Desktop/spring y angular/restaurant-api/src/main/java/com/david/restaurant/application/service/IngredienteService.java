package com.david.restaurant.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.david.restaurant.domain.exception.ingrediente.IngredienteAlreadyExistsException;
import com.david.restaurant.domain.exception.ingrediente.IngredienteNotFoundException;
import com.david.restaurant.domain.model.Ingrediente;
import com.david.restaurant.domain.port.input.IngredienteServicePort;
import com.david.restaurant.domain.port.output.IngredienteRepositoryPort;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class IngredienteService implements IngredienteServicePort{

    private final IngredienteRepositoryPort ingredienteRepositoryPort;

    @Transactional
    public Ingrediente saveIngrediente(Ingrediente ingrediente){
        if (ingredienteRepositoryPort.existsByNombre(ingrediente.getNombre())){
            throw new IngredienteAlreadyExistsException(ingrediente.getNombre());
        }
        return ingredienteRepositoryPort.save(ingrediente);
    }

    @Transactional
    (readOnly = true)
    public List<Ingrediente> findAll(){
        return ingredienteRepositoryPort.findAll();
    }

    @Transactional
    (readOnly = true)
    public Ingrediente findById(Long id){
        return ingredienteRepositoryPort.findById(id)
        .orElseThrow(()-> new IngredienteNotFoundException(id));
    }

    @Transactional
    public void deleteById(Long id){
        if (!ingredienteRepositoryPort.existsById(id)){
            throw new IngredienteNotFoundException(id);
        }
        ingredienteRepositoryPort.deleteById(id);
    }

    @Transactional
    public Ingrediente updateIngrediente(Long id, Ingrediente ingredienteUpdate){
        return ingredienteRepositoryPort.findById(id).map(ingredienteBd -> {
            if (ingredienteUpdate.getNombre() != null){
                ingredienteBd.setNombre(ingredienteUpdate.getNombre());
            }
            if(ingredienteUpdate.getPrecio() != null){
                ingredienteBd.setPrecio(ingredienteUpdate.getPrecio());
            }
            return ingredienteRepositoryPort.save(ingredienteBd);
        }).orElseThrow(()-> new IngredienteNotFoundException(id));
    }

    


    
}
