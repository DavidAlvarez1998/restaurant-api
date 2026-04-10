package com.david.restaurant.domain.port.output;

import com.david.restaurant.domain.model.Producto;
import java.util.List;
import java.util.Optional;

public interface ProductoRepositoryPort {
    
    Producto save(Producto producto);
    List<Producto> findAll();
    Optional<Producto> findById(Long id);
    void deleteById(Long id);
    Boolean existsById(Long id);
    Boolean existsByNombre(String nombre);
}
