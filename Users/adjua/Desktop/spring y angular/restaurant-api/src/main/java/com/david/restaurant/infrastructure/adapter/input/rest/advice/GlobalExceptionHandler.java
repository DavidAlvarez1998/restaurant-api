package com.david.restaurant.infrastructure.adapter.input.rest.advice;

import com.david.restaurant.domain.exception.ingrediente.IngredienteAlreadyExistsException;
import com.david.restaurant.domain.exception.ingrediente.IngredienteNotFoundException;
import com.david.restaurant.domain.exception.mesa.MesaAlreadyExistsException;
import com.david.restaurant.domain.exception.mesa.MesaNotFoundException;
import com.david.restaurant.domain.exception.producto.ProductoAlreadyExistsException;
import com.david.restaurant.domain.exception.producto.ProductoInvalidTypeException;
import com.david.restaurant.domain.exception.producto.ProductoNotFoundException;
import com.david.restaurant.domain.exception.orden.OrdenNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Manejador para errores inesperados (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
            "ERROR_INTERNO",
            "Ocurrió un error inesperado.",
            LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // --- EXCEPCIONES DE MESA ---

    @ExceptionHandler(MesaAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleMesaAlreadyExists(MesaAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse("MESA_DUPLICADA", ex.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT); // 409
    }

    @ExceptionHandler(MesaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMesaNotFound(MesaNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("MESA_NO_ENCONTRADA", ex.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND); // 404
    }

    // --- EXCEPCIONES DE INGREDIENTE ---

    @ExceptionHandler(IngredienteAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleIngredienteAlreadyExists(IngredienteAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse("INGREDIENTE_DUPLICADO", ex.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT); // 409
    }

    @ExceptionHandler(IngredienteNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleIngredienteNotFound(IngredienteNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("INGREDIENTE_NO_ENCONTRADO", ex.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND); // 404
    }

    // --- EXCEPCIONES DE PRODUCTO ---

    @ExceptionHandler(ProductoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductoNotFound(ProductoNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("PRODUCTO_NO_ENCONTRADO", ex.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND); // 404
    }

    @ExceptionHandler(ProductoAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleProductoAlreadyExists(ProductoAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse("PRODUCTO_DUPLICADO", ex.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT); // 409
    }

    @ExceptionHandler(ProductoInvalidTypeException.class)
    public ResponseEntity<ErrorResponse> handleProductoInvalidType(ProductoInvalidTypeException ex) {
        ErrorResponse error = new ErrorResponse("TIPO_PRODUCTO_INVALIDO", ex.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST); // 400
    }

    // --- EXCEPCIONES DE ORDEN ---

    @ExceptionHandler(OrdenNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrdenNotFound(OrdenNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("ORDEN_NO_ENCONTRADA", ex.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND); // 404
    }

    // --- EXCEPCIONES DE REGLAS DE NEGOCIO ---
    
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ErrorResponse> handleBusinessValidationException(RuntimeException ex) {
        ErrorResponse error = new ErrorResponse("ERROR_DE_VALIDACION", ex.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST); // 400
    }

}
