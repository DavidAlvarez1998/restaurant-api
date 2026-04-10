package com.david.restaurant.infrastructure.adapter.output.persistence.ingrediente;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredienteRepository extends JpaRepository<IngredienteEntity, Long>{
    Boolean existsByNombre(String nombre);
} 
    
