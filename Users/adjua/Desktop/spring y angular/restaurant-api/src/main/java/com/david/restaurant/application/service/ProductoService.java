package com.david.restaurant.application.service;

import com.david.restaurant.domain.exception.producto.ProductoAlreadyExistsException;
import com.david.restaurant.domain.exception.producto.ProductoNotFoundException;
import com.david.restaurant.domain.model.Producto;
import com.david.restaurant.domain.port.input.ProductoServicePort;
import com.david.restaurant.domain.port.output.ProductoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService implements ProductoServicePort {

    private final ProductoRepositoryPort productoRepositoryPort;

    @Override
    @Transactional
    public Producto save(Producto producto) {
        if (productoRepositoryPort.existsByNombre(producto.getNombre())) {
            throw new ProductoAlreadyExistsException(producto.getNombre());
        }
        return productoRepositoryPort.save(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findAll() {
        return productoRepositoryPort.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Producto findById(Long id) {
        return productoRepositoryPort.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!productoRepositoryPort.existsById(id)) {
            throw new ProductoNotFoundException(id);
        }
        productoRepositoryPort.deleteById(id);
    }

    @Override
    @Transactional
    public Producto update(Long id, Producto productoUpdate) {
        return productoRepositoryPort.findById(id).map(productoExistente -> {
            actualizarCamposBasicos(productoExistente, productoUpdate);

            if (productoUpdate.getTipo() != null) {
                productoExistente.setTipo(productoUpdate.getTipo());
            }

            return productoRepositoryPort.save(productoExistente);
        }).orElseThrow(() -> new ProductoNotFoundException(id));
    }


    private void actualizarCamposBasicos(Producto existente, Producto nuevo) {
        if (nuevo.getNombre() != null) existente.setNombre(nuevo.getNombre());
        if (nuevo.getDescripcion() != null) existente.setDescripcion(nuevo.getDescripcion());
        if (nuevo.getPrecio() != null) existente.setPrecio(nuevo.getPrecio());
    }
}
