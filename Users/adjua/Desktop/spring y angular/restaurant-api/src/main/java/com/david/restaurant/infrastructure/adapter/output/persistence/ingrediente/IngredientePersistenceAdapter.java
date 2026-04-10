package com.david.restaurant.infrastructure.adapter.output.persistence.ingrediente;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.david.restaurant.domain.model.Ingrediente;
import com.david.restaurant.domain.port.output.IngredienteRepositoryPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IngredientePersistenceAdapter implements IngredienteRepositoryPort{
    
    private final IngredienteRepository ingredienteRepository;
    private final  IngredienteMapper ingredienteMapper;

    @Override
    public Ingrediente save(Ingrediente ingrediente){
        IngredienteEntity ingredienteEntity = ingredienteMapper.toEntity(ingrediente);
        IngredienteEntity saveEntity = ingredienteRepository.save(ingredienteEntity);
        return ingredienteMapper.toDomain(saveEntity);
    }

    @Override
    public List<Ingrediente> findAll(){
        return ingredienteRepository.findAll().stream().map(ingredienteMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Ingrediente>findById(Long id){
        return ingredienteRepository.findById(id).map(ingredienteMapper::toDomain);
    }

    @Override
    public void deleteById(Long id){
        ingredienteRepository.deleteById(id);
    }

    @Override
    public Boolean existsById(Long id){
        return ingredienteRepository.existsById(id);
    }

    @Override
    public Boolean existsByNombre(String nombre){
        return ingredienteRepository.existsByNombre(nombre);
    }



}
