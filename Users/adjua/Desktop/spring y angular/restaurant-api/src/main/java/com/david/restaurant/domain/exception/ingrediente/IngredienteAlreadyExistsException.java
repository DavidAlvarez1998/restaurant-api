package com.david.restaurant.domain.exception.ingrediente;

public class IngredienteAlreadyExistsException extends RuntimeException{

    public IngredienteAlreadyExistsException(String nombre){
        super("Ya existe un ingrediente con el nombre: " + nombre);
    }
}
