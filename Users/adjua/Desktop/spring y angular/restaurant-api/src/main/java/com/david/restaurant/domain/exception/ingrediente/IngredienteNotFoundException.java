package com.david.restaurant.domain.exception.ingrediente;

public class IngredienteNotFoundException extends RuntimeException{
    public IngredienteNotFoundException(Long id){
        super("no se encotro ningun ingrediente con el ID: " + id);
    }
}
