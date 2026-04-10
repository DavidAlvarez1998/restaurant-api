package com.david.restaurant.domain.port.output;

import com.david.restaurant.domain.model.Orden;
import java.util.List;
import java.util.Optional;

public interface OrdenRepositoryPort {
    Orden save(Orden orden);
    List<Orden> findAll();
    Optional<Orden> findById(Long id);
    void deleteById(Long id);         
    boolean existsById(Long id); 
}
