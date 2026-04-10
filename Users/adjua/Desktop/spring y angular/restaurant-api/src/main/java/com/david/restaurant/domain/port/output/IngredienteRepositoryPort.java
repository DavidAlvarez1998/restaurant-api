package com.david.restaurant.domain.port.output;

import java.util.List;
import java.util.Optional;

import com.david.restaurant.domain.model.Ingrediente;

public interface IngredienteRepositoryPort {

    Ingrediente save(Ingrediente ingrediente);
    List<Ingrediente> findAll();
    Optional<Ingrediente> findById(Long id);
    void deleteById(Long id);
    Boolean existsById(Long id);
    Boolean existsByNombre(String nombre);  
}
