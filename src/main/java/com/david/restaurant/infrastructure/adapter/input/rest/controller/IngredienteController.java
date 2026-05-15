package com.david.restaurant.infrastructure.adapter.input.rest.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.david.restaurant.domain.model.Ingrediente;
import com.david.restaurant.domain.port.input.IngredienteServicePort;
import com.david.restaurant.infrastructure.adapter.input.rest.dto.ingredienteDto.IngredienteRequest;
import com.david.restaurant.infrastructure.adapter.input.rest.dto.ingredienteDto.IngredienteResponse;
import com.david.restaurant.infrastructure.adapter.input.rest.mapper.IngredienteRestMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ingredientes")
@RequiredArgsConstructor
public class IngredienteController {

    private final IngredienteServicePort ingredienteServicePort;
    private final IngredienteRestMapper ingredienteRestMapper;


    @PostMapping
    public ResponseEntity<IngredienteResponse> save(@RequestBody IngredienteRequest request){
        Ingrediente ingredienteDomain = ingredienteRestMapper.toDomain(request);
        Ingrediente ingredienteCreado = ingredienteServicePort.saveIngrediente(ingredienteDomain);
        return new ResponseEntity<>(ingredienteRestMapper.toResponse(ingredienteCreado),HttpStatus.CREATED);
    }

    @GetMapping 
    public ResponseEntity<List<IngredienteResponse>> findAll(){
     List<IngredienteResponse> ingredientes =ingredienteServicePort.findAll()
            .stream()
            .map(ingredienteRestMapper::toResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(ingredientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredienteResponse> findByid(@PathVariable Long id){
        Ingrediente ingrediente = ingredienteServicePort.findById(id);
        return ResponseEntity.ok(ingredienteRestMapper.toResponse(ingrediente));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngredienteResponse> update(@PathVariable Long id, @RequestBody IngredienteRequest request){
        Ingrediente ingredienteUpdate = ingredienteRestMapper.toDomain(request);        
        Ingrediente ingredientActualizado = ingredienteServicePort.updateIngrediente(id, ingredienteUpdate);
        return ResponseEntity.ok(ingredienteRestMapper.toResponse(ingredientActualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        ingredienteServicePort.deleteById(id);
        return ResponseEntity.noContent().build();
    }




    
}
