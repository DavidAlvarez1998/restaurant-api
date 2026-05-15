package com.david.restaurant.infrastructure.adapter.output.persistence.producto;

import com.david.restaurant.domain.model.Producto;
import com.david.restaurant.domain.port.output.ProductoRepositoryPort;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductoPersistenceAdapter implements ProductoRepositoryPort {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper; 

    @Override
    public Producto save(Producto producto) {
        ProductoEntity entity = productoMapper.toEntity(producto);
        ProductoEntity savedEntity = productoRepository.save(entity);
        return productoMapper.toDomain(savedEntity);
    }

    @Override
    public List<Producto> findAll() {
        return productoRepository.findAll().stream()
                .map(productoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id)
                .map(productoMapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        productoRepository.deleteById(id);
    }

    @Override
    public Boolean existsByNombre(String nombre){
        return productoRepository.existsByNombre(nombre);
    }

    @Override
    public Boolean existsById(Long id){
        return productoRepository.existsById(id);
    }

}