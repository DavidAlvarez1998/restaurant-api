package com.david.restaurant.domain.port.input;

import com.david.restaurant.domain.model.Producto;
import java.util.List;

public interface ProductoServicePort {

    Producto save(Producto producto);
    List<Producto> findAll();
    Producto findById(Long id);
    void deleteById(Long id);
    Producto update(Long id, Producto productoUpdate);
}