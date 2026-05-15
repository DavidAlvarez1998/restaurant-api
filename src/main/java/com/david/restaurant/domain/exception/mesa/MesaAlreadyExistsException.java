package com.david.restaurant.domain.exception.mesa;

public class MesaAlreadyExistsException extends RuntimeException {
    public MesaAlreadyExistsException(String numero) {
        super("La mesa con el número " + numero + " ya existe en el sistema.");
    }
}
