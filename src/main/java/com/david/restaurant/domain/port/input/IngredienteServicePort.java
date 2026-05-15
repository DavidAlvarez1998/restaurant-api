package com.david.restaurant.domain.port.input;

import java.util.List;
import com.david.restaurant.domain.model.Ingrediente;

public interface IngredienteServicePort {
    Ingrediente saveIngrediente(Ingrediente ingrediente);
    List<Ingrediente> findAll();
    Ingrediente findById(Long id);
    void deleteById(Long id);
    Ingrediente updateIngrediente(Long id, Ingrediente ingredienteUpdate);
}